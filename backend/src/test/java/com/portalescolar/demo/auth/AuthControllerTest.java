package com.portalescolar.demo.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portalescolar.demo.AbstractIntegrationTest;
import com.portalescolar.demo.TestUtils;
import com.portalescolar.auth.dto.LoginRequestDto;
import com.portalescolar.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Auth — testes de integração")
public class AuthControllerTest extends AbstractIntegrationTest {
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // garante que o admin existe antes de cada teste
        TestUtils.createAdmin(userRepository, passwordEncoder);
    }

    // ------------------------------------------------------------------ login

    @Test
    @DisplayName("POST /api/auth/login — credenciais válidas → 200 com token")
    void loginComCredenciaisValidas_retorna200ComToken() throws Exception {
        var dto = new LoginRequestDto(TestUtils.ADMIN_EMAIL, TestUtils.ADMIN_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(TestUtils.ADMIN_EMAIL));
    }

    @Test
    @DisplayName("POST /api/auth/login — senha errada → 422")
    void loginComSenhaErrada_retorna422() throws Exception {
        var dto = new LoginRequestDto(TestUtils.ADMIN_EMAIL, "senha-errada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Email ou senha inválidos."));
    }

    @Test
    @DisplayName("POST /api/auth/login — email inexistente → 422")
    void loginComEmailInexistente_retorna422() throws Exception {
        var dto = new LoginRequestDto("naoexiste@teste.com", "qualquersenha");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /api/auth/login — usuário inativo → 422")
    void loginComUsuarioInativo_retorna422() throws Exception {
        // cria user e desativa direto no banco
        var user = TestUtils.createUser(userRepository, passwordEncoder);
        user.setActive(false);
        userRepository.save(user);

        var dto = new LoginRequestDto(TestUtils.USER_EMAIL, TestUtils.USER_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Usuário desativado. Entre em contato com o administrador."));

        // reativa para não sujar outros testes
        user.setActive(true);
        userRepository.save(user);
    }

    @Test
    @DisplayName("POST /api/auth/login — body sem email → 400 validation error")
    void loginSemEmail_retorna400() throws Exception {
        // email em branco, deveria falhar na validação @NotBlank @Email
        String body = """
                {"email":"","password":"senha1234"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login — body sem senha → 400 validation error")
    void loginSemSenha_retorna400() throws Exception {
        String body = """
                {"email":"admin@teste.com","password":""}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

}
