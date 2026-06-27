package com.portalescolar.demo.news;
import com.portalescolar.news.dto.NewsRequestDto;
import com.portalescolar.news.dto.NewsResponseDto;
import com.portalescolar.news.entity.News;
import com.portalescolar.news.entity.NewsStatus;
import com.portalescolar.news.mapper.NewsMapper;
import com.portalescolar.news.repository.NewsRepository;
import com.portalescolar.news.service.NewsService;
import com.portalescolar.shared.exception.BusinessRuleException;
import com.portalescolar.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NewsService — testes de unidade")
public class NewsServiceTest {
    @Mock NewsRepository newsRepository;
    @Mock NewsMapper newsMapper;

    @InjectMocks NewsService newsService;

    private UUID newsId;
    private News draftNews;
    private News publishedNews;
    private News archivedNews;
    private NewsResponseDto fakeDto;

    @BeforeEach
    void setUp() {
        newsId = UUID.randomUUID();

        draftNews = News.builder()
                .id(newsId)
                .title("Rascunho")
                .body("Corpo")
                .newsStatus(NewsStatus.DRAFT)
                .build();

        publishedNews = News.builder()
                .id(newsId)
                .title("Publicada")
                .body("Corpo")
                .newsStatus(NewsStatus.PUBLISHED)
                .build();

        archivedNews = News.builder()
                .id(newsId)
                .title("Arquivada")
                .body("Corpo")
                .newsStatus(NewsStatus.ARCHIVED)
                .build();

        fakeDto = new NewsResponseDto(newsId, "Título", "Sub", "Corpo",
                null, "DRAFT", null, null);
    }

    // ------------------------------------------------------------------ save

    @Test
    @DisplayName("save — sempre cria com status DRAFT")
    void save_sempreComDraft() {
        var dto = new NewsRequestDto("Título", "Sub", "Corpo", null);
        given(newsMapper.toEntity(dto)).willReturn(draftNews);
        given(newsRepository.save(draftNews)).willReturn(draftNews);
        given(newsMapper.toResponseDto(draftNews)).willReturn(fakeDto);

        newsService.save(dto);

        // confirma que o status foi forçado para DRAFT antes de salvar
        assertThat(draftNews.getNewsStatus()).isEqualTo(NewsStatus.DRAFT);
        then(newsRepository).should().save(draftNews);
    }

    // ------------------------------------------------------------------ findAll

    @Test
    @DisplayName("findAll — público com status=DRAFT → lança BusinessRuleException (acesso negado)")
    void findAll_publicoComDraft_lancaException() {
        assertThatThrownBy(() -> newsService.findAll(null, "DRAFT", false))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Acesso negado.");
    }

    @Test
    @DisplayName("findAll — status inválido → lança BusinessRuleException")
    void findAll_statusInvalido_lancaException() {
        assertThatThrownBy(() -> newsService.findAll(null, "INVALIDO", false))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Status inválido");
    }

    // ------------------------------------------------------------------ findById

    @Test
    @DisplayName("findById — DRAFT como público → lança ResourceNotFoundException")
    void findById_draftPublico_lancaException() {
        given(newsRepository.findAllByIdAndNewsStatus(newsId, NewsStatus.PUBLISHED))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.findById(newsId, false))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findById — qualquer status como admin → retorna DTO")
    void findById_draftComoAdmin_retornaDto() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(draftNews));
        given(newsMapper.toResponseDto(draftNews)).willReturn(fakeDto);

        var result = newsService.findById(newsId, true);

        assertThat(result).isNotNull();
    }

    // ------------------------------------------------------------------ update

    @Test
    @DisplayName("update — ARCHIVED → lança BusinessRuleException")
    void update_archived_lancaException() {
        var dto = new NewsRequestDto("Novo", null, "Corpo", null);
        given(newsRepository.findById(newsId)).willReturn(Optional.of(archivedNews));

        assertThatThrownBy(() -> newsService.update(newsId, dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Não é possível editar uma notícia arquivada.");
    }

    @Test
    @DisplayName("update — DRAFT → atualiza e retorna DTO")
    void update_draft_retornaDto() {
        var dto = new NewsRequestDto("Atualizado", null, "Corpo", null);
        given(newsRepository.findById(newsId)).willReturn(Optional.of(draftNews));
        given(newsRepository.save(draftNews)).willReturn(draftNews);
        given(newsMapper.toResponseDto(draftNews)).willReturn(fakeDto);

        assertThatNoException().isThrownBy(() -> newsService.update(newsId, dto));
    }

    // ------------------------------------------------------------------ publish

    @Test
    @DisplayName("publish — DRAFT → muda para PUBLISHED e seta publishedAt")
    void publish_draft_mudastatusESetaPublishedAt() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(draftNews));
        given(newsRepository.save(draftNews)).willReturn(draftNews);
        given(newsMapper.toResponseDto(draftNews)).willReturn(
                new NewsResponseDto(newsId, "T", null, "C", null, "PUBLISHED", "2025-01-01T10:00", null));

        newsService.publish(newsId);

        assertThat(draftNews.getNewsStatus()).isEqualTo(NewsStatus.PUBLISHED);
        assertThat(draftNews.getPublishedAt()).isNotNull();
    }

    @Test
    @DisplayName("publish — PUBLISHED tenta publicar de novo → lança BusinessRuleException")
    void publish_publishedDeNovo_lancaException() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(publishedNews));

        assertThatThrownBy(() -> newsService.publish(newsId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Apenas rascunhos podem ser publicados.");
    }

    @Test
    @DisplayName("publish — ARCHIVED tenta publicar → lança BusinessRuleException")
    void publish_archivedTentaPublicar_lancaException() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(archivedNews));

        assertThatThrownBy(() -> newsService.publish(newsId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Apenas rascunhos podem ser publicados.");
    }

    // ------------------------------------------------------------------ archive

    @Test
    @DisplayName("archive — PUBLISHED → ARCHIVED")
    void archive_published_retornaArchived() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(publishedNews));
        given(newsRepository.save(publishedNews)).willReturn(publishedNews);
        given(newsMapper.toResponseDto(publishedNews)).willReturn(fakeDto);

        newsService.archive(newsId);

        assertThat(publishedNews.getNewsStatus()).isEqualTo(NewsStatus.ARCHIVED);
    }

    @Test
    @DisplayName("archive — já ARCHIVED → lança BusinessRuleException")
    void archive_archivedDeNovo_lancaException() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(archivedNews));

        assertThatThrownBy(() -> newsService.archive(newsId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Notícia já está arquivada.");
    }

    // ------------------------------------------------------------------ backToDraft

    @Test
    @DisplayName("backToDraft — ARCHIVED → DRAFT e limpa publishedAt")
    void backToDraft_archived_viraDraft() {
        // simula published para ter publishedAt populado
        archivedNews.publish();
        archivedNews.archive();

        given(newsRepository.findById(newsId)).willReturn(Optional.of(archivedNews));
        given(newsRepository.save(archivedNews)).willReturn(archivedNews);
        given(newsMapper.toResponseDto(archivedNews)).willReturn(fakeDto);

        newsService.backToDraft(newsId);

        assertThat(archivedNews.getNewsStatus()).isEqualTo(NewsStatus.DRAFT);
        assertThat(archivedNews.getPublishedAt()).isNull();
    }

    @Test
    @DisplayName("backToDraft — PUBLISHED tenta voltar → lança BusinessRuleException")
    void backToDraft_published_lancaException() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(publishedNews));

        assertThatThrownBy(() -> newsService.backToDraft(newsId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Apenas notícias arquivadas podem voltar para rascunho.");
    }

    // ------------------------------------------------------------------ delete

    @Test
    @DisplayName("delete — DRAFT → deletado sem exceção")
    void delete_draft_deletaSemExcecao() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(draftNews));
        willDoNothing().given(newsRepository).delete(draftNews);

        assertThatNoException().isThrownBy(() -> newsService.delete(newsId));
        then(newsRepository).should().delete(draftNews);
    }

    @Test
    @DisplayName("delete — PUBLISHED → lança BusinessRuleException")
    void delete_published_lancaException() {
        given(newsRepository.findById(newsId)).willReturn(Optional.of(publishedNews));

        assertThatThrownBy(() -> newsService.delete(newsId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Apenas rascunhos podem ser excluídos.");
    }

    @Test
    @DisplayName("delete — inexistente → lança ResourceNotFoundException")
    void delete_inexistente_lancaException() {
        given(newsRepository.findById(newsId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.delete(newsId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
