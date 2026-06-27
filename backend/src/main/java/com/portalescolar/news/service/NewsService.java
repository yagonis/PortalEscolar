package com.portalescolar.news.service;

import com.portalescolar.news.dto.NewsRequestDto;
import com.portalescolar.news.dto.NewsResponseDto;
import com.portalescolar.news.entity.News;
import com.portalescolar.news.entity.NewsStatus;
import com.portalescolar.news.mapper.NewsMapper;
import com.portalescolar.news.repository.NewsRepository;
import com.portalescolar.shared.exception.BusinessRuleException;
import com.portalescolar.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Transactional
    public NewsResponseDto save(NewsRequestDto dto) {
        News news = newsMapper.toEntity(dto);
        news.setNewsStatus(NewsStatus.DRAFT);
        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Transactional(readOnly = true)
    public Page<NewsResponseDto> findAll(Pageable pageable, String status, boolean isAdmin) {
        if (status != null) {
            NewsStatus statusEnum;
            try {
                statusEnum = NewsStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Status inválido: " + status);
            }

            if (!isAdmin && statusEnum != NewsStatus.PUBLISHED) {
                throw new BusinessRuleException("Acesso negado.");
            }

            return newsRepository.findAllByNewsStatusOrderByPublishedAtDesc(statusEnum, pageable)
                    .map(newsMapper::toResponseDto);
        }

        if (isAdmin) {
            return newsRepository.findAll(pageable)
                    .map(newsMapper::toResponseDto);
        }

        return newsRepository.findAllByNewsStatusOrderByPublishedAtDesc(NewsStatus.PUBLISHED, pageable)
                .map(newsMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public NewsResponseDto findById(UUID id, boolean isAdmin) {
        if (isAdmin) {
            News news = newsRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada."));
            return newsMapper.toResponseDto(news);
        }

        News news = newsRepository.findAllByIdAndNewsStatus(id, NewsStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada."));
        return newsMapper.toResponseDto(news);
    }

    @Transactional
    public NewsResponseDto update(UUID id, NewsRequestDto dto) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada."));

        if (news.getNewsStatus() == NewsStatus.ARCHIVED) {
            throw new BusinessRuleException("Não é possível editar uma notícia arquivada.");
        }

        newsMapper.updateEntityFromDto(dto, news);
        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Transactional
    public NewsResponseDto publish(UUID id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada."));

        if (!news.isDraft()) {
            throw new BusinessRuleException("Apenas rascunhos podem ser publicados.");
        }

        news.publish();
        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Transactional
    public NewsResponseDto archive(UUID id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada."));

        if (news.getNewsStatus() == NewsStatus.ARCHIVED) {
            throw new BusinessRuleException("Notícia já está arquivada.");
        }

        news.archive();
        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Transactional
    public NewsResponseDto backToDraft(UUID id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada."));

        if (!news.getNewsStatus().equals(NewsStatus.ARCHIVED)) {
            throw new BusinessRuleException("Apenas notícias arquivadas podem voltar para rascunho.");
        }

        news.backToDraft();
        return newsMapper.toResponseDto(newsRepository.save(news));
    }

    @Transactional
    public void delete(UUID id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notícia não encontrada."));

        if (!news.isDraft()) {
            throw new BusinessRuleException("Apenas rascunhos podem ser excluídos.");
        }

        newsRepository.delete(news);
    }
}