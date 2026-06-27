package com.portalescolar.demo.user;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portalescolar.demo.AbstractIntegrationTest;
import com.portalescolar.demo.TestUtils;
import com.portalescolar.auth.dto.LoginRequestDto;
import com.portalescolar.user.entity.User;
import com.portalescolar.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("User — testes de integração")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest extends AbstractIntegrationTest{
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;
    private User adminUser;

    // ------------------------------------------------------------------ setup

    @BeforeEach
    void setUp() throws Exception {
        adminUser = TestUtils.createAdmin(userRepository, passwordEncoder);
        TestUtils.createUser(userRepository, passwordEncoder);
        adminToken = obterToken(TestUtils.ADMIN_EMAIL, TestUtils.ADMIN_PASSWORD);
        userToken  = obterToken(TestUtils.USER_EMAIL, TestUtils.USER_PASSWORD);
    }

    private String obterToken(String email, String password) throws Exception {
        var dto = new LoginRequestDto(email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        var tree = objectMapper.readTree(result.getResponse().getContentAsString());
        return tree.get("token").asText();
    }

    // ------------------------------------------------------------------ GET /api/users

    @Test
    @Order(1)
    @DisplayName("GET /api/users — admin → 200 com lista paginada")
    void findAll_comoAdmin_retorna200() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/users — sem token → 401")
    void findAll_semToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/users — USER comum → 403")
    void findAll_comoUser_retorna403() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/users?role=USER — filtra por role")
    void findAll_filtrandoPorRole_retornaApenasAqueleRole() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("role", "USER")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].role").value(
                        org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.is("USER"))));
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/users?role=INVALIDA — role inválida → 422")
    void findAll_roleInvalida_retorna422() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("role", "INVALIDA")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isUnprocessableEntity());
    }

    // ------------------------------------------------------------------ GET /api/users/{id}

    @Test
    @Order(6)
    @DisplayName("GET /api/users/{id} — admin → 200")
    void findById_existente_retorna200() throws Exception {
        mockMvc.perform(get("/api/users/{id}", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TestUtils.ADMIN_EMAIL));
    }

    @Test
    @Order(7)
    @DisplayName("GET /api/users/{id} — id inexistente → 404")
    void findById_inexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/users/{id}", java.util.UUID.randomUUID())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    // ------------------------------------------------------------------ POST /api/users

    @Test
    @Order(8)
    @DisplayName("POST /api/users — admin cria USER → 201")
    void save_adminCriaUser_retorna201() throws Exception {
        String body = """
                {
                  "name": "Novo Aluno",
                  "email": "aluno.novo@teste.com",
                  "password": "senha1234",
                  "role": "USER"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("aluno.novo@teste.com"))
                .andExpect(jsonPath("$.active").value(true));

        // limpeza
        userRepository.findByEmail("aluno.novo@teste.com").ifPresent(userRepository::delete);
    }

    @Test
    @Order(9)
    @DisplayName("POST /api/users — email duplicado → 422")
    void save_emailDuplicado_retorna422() throws Exception {
        String body = """
                {
                  "name": "Duplicado",
                  "email": "%s",
                  "password": "senha1234",
                  "role": "USER"
                }
                """.formatted(TestUtils.USER_EMAIL);

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("E-mail já cadastrado."));
    }

    @Test
    @Order(10)
    @DisplayName("POST /api/users — senha curta → 400 validation")
    void save_senhaCurta_retorna400() throws Exception {
        String body = """
                {
                  "name": "Teste",
                  "email": "valido@teste.com",
                  "password": "123",
                  "role": "USER"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    @DisplayName("POST /api/users — USER tenta criar usuário → 403")
    void save_userComum_retorna403() throws Exception {
        String body = """
                {
                  "name": "Tentativa",
                  "email": "tentativa@teste.com",
                  "password": "senha1234",
                  "role": "USER"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    // ------------------------------------------------------------------ PUT /api/users/{id}

    @Test
    @Order(12)
    @DisplayName("PUT /api/users/{id} — atualiza nome → 200")
    void update_dadosValidos_retorna200() throws Exception {
        String body = """
                {
                  "name": "Admin Atualizado",
                  "email": "%s"
                }
                """.formatted(TestUtils.ADMIN_EMAIL);

        mockMvc.perform(put("/api/users/{id}", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin Atualizado"));
    }

    @Test
    @Order(13)
    @DisplayName("PUT /api/users/{id} — email de outro usuário → 422")
    void update_emailDeOutroUsuario_retorna422() throws Exception {
        // tenta mudar o email do admin para o email do user comum
        String body = """
                {
                  "name": "Admin",
                  "email": "%s"
                }
                """.formatted(TestUtils.USER_EMAIL);

        mockMvc.perform(put("/api/users/{id}", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("E-mail já está em uso por outro usuário."));
    }

    // ------------------------------------------------------------------ PATCH /{id}/password

    @Test
    @Order(14)
    @DisplayName("PATCH /api/users/{id}/password — troca de senha válida → 204")
    void updatePassword_dadosValidos_retorna204() throws Exception {
        String body = """
                {
                  "currentPassword": "%s",
                  "newPassword": "novaSenha99",
                  "passwordConfirmation": "novaSenha99"
                }
                """.formatted(TestUtils.ADMIN_PASSWORD);

        mockMvc.perform(patch("/api/users/{id}/password", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        // restaura a senha original para não quebrar outros testes
        String restore = """
                {
                  "currentPassword": "novaSenha99",
                  "newPassword": "%s",
                  "passwordConfirmation": "%s"
                }
                """.formatted(TestUtils.ADMIN_PASSWORD, TestUtils.ADMIN_PASSWORD);

        mockMvc.perform(patch("/api/users/{id}/password", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(restore))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(15)
    @DisplayName("PATCH /api/users/{id}/password — senha atual errada → 422")
    void updatePassword_senhaAtualErrada_retorna422() throws Exception {
        String body = """
                {
                  "currentPassword": "senhaErrada",
                  "newPassword": "novaSenha99",
                  "passwordConfirmation": "novaSenha99"
                }
                """;

        mockMvc.perform(patch("/api/users/{id}/password", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Senha atual incorreta."));
    }

    @Test
    @Order(16)
    @DisplayName("PATCH /api/users/{id}/password — confirmação não confere → 422")
    void updatePassword_confirmacaoDivergente_retorna422() throws Exception {
        String body = """
                {
                  "currentPassword": "%s",
                  "newPassword": "novaSenha99",
                  "passwordConfirmation": "outraSenha99"
                }
                """.formatted(TestUtils.ADMIN_PASSWORD);

        mockMvc.perform(patch("/api/users/{id}/password", adminUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Nova senha e confirmação não conferem."));
    }

    // ------------------------------------------------------------------ PATCH /{id}/status

    @Test
    @Order(17)
    @DisplayName("PATCH /api/users/{id}/status — toggle ativa/desativa → 200")
    void toggleActive_alterna_retorna200() throws Exception {
        var user = userRepository.findByEmail(TestUtils.USER_EMAIL).orElseThrow();
        boolean estadoOriginal = user.getActive();

        MvcResult result = mockMvc.perform(patch("/api/users/{id}/status", user.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        var tree = objectMapper.readTree(result.getResponse().getContentAsString());
        boolean novoEstado = tree.get("active").asBoolean();

        org.junit.jupiter.api.Assertions.assertNotEquals(estadoOriginal, novoEstado,
                "toggleActive deve inverter o estado de 'active'");

        // restaura para não afetar outros testes
        mockMvc.perform(patch("/api/users/{id}/status", user.getId())
                .header("Authorization", "Bearer " + adminToken));
    }
}
