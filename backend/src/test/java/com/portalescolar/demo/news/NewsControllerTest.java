package com.portalescolar.demo.news;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portalescolar.demo.AbstractIntegrationTest;
import com.portalescolar.demo.TestUtils;
import com.portalescolar.auth.dto.LoginRequestDto;
import com.portalescolar.news.entity.News;
import com.portalescolar.news.entity.NewsStatus;
import com.portalescolar.news.repository.NewsRepository;
import com.portalescolar.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@DisplayName("News — testes de integração")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewsControllerTest extends AbstractIntegrationTest{
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired NewsRepository newsRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    // ------------------------------------------------------------------ setup

    @BeforeEach
    void setUp() throws Exception {
        TestUtils.createAdmin(userRepository, passwordEncoder);
        TestUtils.createUser(userRepository, passwordEncoder);
        adminToken = obterToken(TestUtils.ADMIN_EMAIL, TestUtils.ADMIN_PASSWORD);
        userToken  = obterToken(TestUtils.USER_EMAIL, TestUtils.USER_PASSWORD);
    }

    private String obterToken(String email, String password) throws Exception {
        var dto = new LoginRequestDto(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    /** Cria uma notícia direto no banco e retorna o ID. */
    private UUID criarNoticiaNoDb(NewsStatus status) {
        News news = News.builder()
                .title("Notícia de teste")
                .subtitle("Subtítulo")
                .body("Corpo da notícia de teste.")
                .newsStatus(status)
                .build();
        if (status == NewsStatus.PUBLISHED) news.publish();
        return newsRepository.save(news).getId();
    }

    // ------------------------------------------------------------------ POST /api/news

    @Test
    @Order(1)
    @DisplayName("POST /api/news — admin cria notícia → 201 com status DRAFT")
    void save_adminCria_retorna201ComStatusDraft() throws Exception {
        String body = """
                {
                  "title": "Nova Notícia",
                  "subtitle": "Subtítulo",
                  "body": "Conteúdo da notícia aqui."
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/news")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn();

        // limpeza
        var id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
        newsRepository.deleteById(UUID.fromString(id));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/news — sem token → 401")
    void save_semToken_retorna401() throws Exception {
        String body = """
                {"title":"Teste","body":"Corpo"}
                """;

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/news — USER comum → 403")
    void save_userComum_retorna403() throws Exception {
        String body = """
                {"title":"Teste","body":"Corpo"}
                """;

        mockMvc.perform(post("/api/news")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/news — título em branco → 400 validation")
    void save_tituloEmBranco_retorna400() throws Exception {
        String body = """
                {"title":"","body":"Corpo"}
                """;

        mockMvc.perform(post("/api/news")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ GET /api/news

    @Test
    @Order(5)
    @DisplayName("GET /api/news — público sem parâmetros → só PUBLISHED")
    void findAll_publicoSemParams_retornaSoPublished() throws Exception {
        UUID draftId = criarNoticiaNoDb(NewsStatus.DRAFT);
        UUID publishedId = criarNoticiaNoDb(NewsStatus.PUBLISHED);

        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].status", everyItem(is("PUBLISHED"))));

        newsRepository.deleteById(draftId);
        newsRepository.deleteById(publishedId);
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/news?isAdmin=true — admin vê todos os status")
    void findAll_adminVeTodos() throws Exception {
        UUID draftId     = criarNoticiaNoDb(NewsStatus.DRAFT);
        UUID publishedId = criarNoticiaNoDb(NewsStatus.PUBLISHED);

        mockMvc.perform(get("/api/news")
                        .param("isAdmin", "true")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(2)));

        newsRepository.deleteById(draftId);
        newsRepository.deleteById(publishedId);
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/news?status=DRAFT — público tenta ver DRAFT → 422 acesso negado")
    void findAll_publicoTentaVerDraft_retorna422() throws Exception {
        mockMvc.perform(get("/api/news").param("status", "DRAFT"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Acesso negado."));
    }

    // ------------------------------------------------------------------ GET /api/news/{id}

    @Test
    @Order(8)
    @DisplayName("GET /api/news/{id} — notícia PUBLISHED → 200 público")
    void findById_published_retorna200Publico() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.PUBLISHED);

        mockMvc.perform(get("/api/news/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        newsRepository.deleteById(id);
    }

    @Test
    @Order(9)
    @DisplayName("GET /api/news/{id} — notícia DRAFT sem isAdmin → 404")
    void findById_draftPublico_retorna404() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);

        mockMvc.perform(get("/api/news/{id}", id))
                .andExpect(status().isNotFound());

        newsRepository.deleteById(id);
    }

    @Test
    @Order(10)
    @DisplayName("GET /api/news/{id}?isAdmin=true — admin vê DRAFT → 200")
    void findById_draftComoAdmin_retorna200() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);

        mockMvc.perform(get("/api/news/{id}", id)
                        .param("isAdmin", "true")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        newsRepository.deleteById(id);
    }

    // ------------------------------------------------------------------ PATCH publish

    @Test
    @Order(11)
    @DisplayName("PATCH /api/news/{id}/publish — DRAFT → PUBLISHED")
    void publish_draftViraPublished() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);

        mockMvc.perform(patch("/api/news/{id}/publish", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        newsRepository.deleteById(id);
    }

    @Test
    @Order(12)
    @DisplayName("PATCH /api/news/{id}/publish — PUBLISHED tenta publicar de novo → 422")
    void publish_publishedDeNovo_retorna422() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.PUBLISHED);

        mockMvc.perform(patch("/api/news/{id}/publish", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Apenas rascunhos podem ser publicados."));

        newsRepository.deleteById(id);
    }

    // ------------------------------------------------------------------ PATCH archive

    @Test
    @Order(13)
    @DisplayName("PATCH /api/news/{id}/archive — PUBLISHED → ARCHIVED")
    void archive_publishedViraArchived() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.PUBLISHED);

        mockMvc.perform(patch("/api/news/{id}/archive", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));

        newsRepository.deleteById(id);
    }

    @Test
    @Order(14)
    @DisplayName("PATCH /api/news/{id}/archive — ARCHIVED de novo → 422")
    void archive_archivedDeNovo_retorna422() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);
        // arquiva via serviço direto no banco para evitar dependência de rota
        News news = newsRepository.findById(id).orElseThrow();
        news.archive();
        newsRepository.save(news);

        mockMvc.perform(patch("/api/news/{id}/archive", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Notícia já está arquivada."));

        newsRepository.deleteById(id);
    }

    // ------------------------------------------------------------------ PATCH draft

    @Test
    @Order(15)
    @DisplayName("PATCH /api/news/{id}/draft — ARCHIVED → DRAFT")
    void backToDraft_archivedViraDraft() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);
        News news = newsRepository.findById(id).orElseThrow();
        news.archive();
        newsRepository.save(news);

        mockMvc.perform(patch("/api/news/{id}/draft", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        newsRepository.deleteById(id);
    }

    @Test
    @Order(16)
    @DisplayName("PATCH /api/news/{id}/draft — DRAFT volta pro draft → 422")
    void backToDraft_draftVoltaPraDraft_retorna422() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);

        mockMvc.perform(patch("/api/news/{id}/draft", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Apenas notícias arquivadas podem voltar para rascunho."));

        newsRepository.deleteById(id);
    }

    // ------------------------------------------------------------------ PUT /api/news/{id}

    @Test
    @Order(17)
    @DisplayName("PUT /api/news/{id} — atualiza DRAFT → 200")
    void update_draft_retorna200() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);

        String body = """
                {
                  "title": "Título Atualizado",
                  "body": "Corpo atualizado."
                }
                """;

        mockMvc.perform(put("/api/news/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título Atualizado"));

        newsRepository.deleteById(id);
    }

    @Test
    @Order(18)
    @DisplayName("PUT /api/news/{id} — ARCHIVED não pode editar → 422")
    void update_archived_retorna422() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);
        News news = newsRepository.findById(id).orElseThrow();
        news.archive();
        newsRepository.save(news);

        String body = """
                {
                  "title": "Título",
                  "body": "Corpo."
                }
                """;

        mockMvc.perform(put("/api/news/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Não é possível editar uma notícia arquivada."));

        newsRepository.deleteById(id);
    }

    // ------------------------------------------------------------------ DELETE /api/news/{id}

    @Test
    @Order(19)
    @DisplayName("DELETE /api/news/{id} — DRAFT → 204")
    void delete_draft_retorna204() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.DRAFT);

        mockMvc.perform(delete("/api/news/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(20)
    @DisplayName("DELETE /api/news/{id} — PUBLISHED não pode deletar → 422")
    void delete_published_retorna422() throws Exception {
        UUID id = criarNoticiaNoDb(NewsStatus.PUBLISHED);

        mockMvc.perform(delete("/api/news/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Apenas rascunhos podem ser excluídos."));

        newsRepository.deleteById(id);
    }

    @Test
    @Order(21)
    @DisplayName("DELETE /api/news/{id} — inexistente → 404")
    void delete_inexistente_retorna404() throws Exception {
        mockMvc.perform(delete("/api/news/{id}", UUID.randomUUID())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }


}
