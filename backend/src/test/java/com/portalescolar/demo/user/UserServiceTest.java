package com.portalescolar.demo.user;
import com.portalescolar.shared.exception.BusinessRuleException;
import com.portalescolar.shared.exception.ResourceNotFoundException;
import com.portalescolar.user.dto.UserPasswordUpdateDto;
import com.portalescolar.user.dto.UserRequestDto;
import com.portalescolar.user.dto.UserResponseDto;
import com.portalescolar.user.dto.UserUpdateRequestDto;
import com.portalescolar.user.entity.Role;
import com.portalescolar.user.entity.User;
import com.portalescolar.user.mapper.UserMapper;
import com.portalescolar.user.repository.UserRepository;
import com.portalescolar.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService — testes de unidade")
public class UserServiceTest {
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserMapper mapper;

    @InjectMocks UserService userService;

    private UUID userId;
    private User adminUser;
    private User normalUser;
    private UserResponseDto fakeResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        adminUser = User.builder()
                .id(userId)
                .name("Admin")
                .email("admin@test.com")
                .password("hashed")
                .role(Role.ADMIN)
                .active(true)
                .build();

        normalUser = User.builder()
                .id(UUID.randomUUID())
                .name("User")
                .email("user@test.com")
                .password("hashed")
                .role(Role.USER)
                .active(true)
                .build();

        fakeResponse = new UserResponseDto(
                userId, "Admin", "admin@test.com", "ADMIN", true, LocalDateTime.now());
    }

    // ------------------------------------------------------------------ save

    @Test
    @DisplayName("save — email já cadastrado → lança BusinessRuleException")
    void save_emailDuplicado_lancaException() {
        var dto = new UserRequestDto("Nome", "admin@test.com", "senha1234", "USER");
        given(userRepository.existsByEmail("admin@test.com")).willReturn(true);

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("E-mail já cadastrado.");
    }

    @Test
    @DisplayName("save — role inválida → lança BusinessRuleException")
    void save_roleInvalida_lancaException() {
        var dto = new UserRequestDto("Nome", "novo@test.com", "senha1234", "PROFESSOR");
        given(userRepository.existsByEmail(anyString())).willReturn(false);

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Role inválida");
    }

    @Test
    @DisplayName("save — USER comum tenta criar ADMIN → lança BusinessRuleException")
    void save_userCriaAdmin_lancaException() {
        var dto = new UserRequestDto("Novo Admin", "novo@test.com", "senha1234", "ADMIN");
        given(userRepository.existsByEmail(anyString())).willReturn(false);

        // simula usuário autenticado com role USER
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        given(ctx.getAuthentication()).willReturn(auth);
        given(auth.getPrincipal()).willReturn(normalUser);
        SecurityContextHolder.setContext(ctx);

        assertThatThrownBy(() -> userService.save(dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Apenas administradores podem criar outros administradores.");

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("save — dados válidos → salva e retorna DTO")
    void save_dadosValidos_salvaNoBanco() {
        var dto = new UserRequestDto("Novo", "novo@test.com", "senha1234", "USER");
        given(userRepository.existsByEmail("novo@test.com")).willReturn(false);
        given(mapper.toEntity(dto)).willReturn(normalUser);
        given(passwordEncoder.encode("senha1234")).willReturn("hashed");
        given(userRepository.save(normalUser)).willReturn(normalUser);
        given(mapper.toResponseDTO(normalUser)).willReturn(
                new UserResponseDto(normalUser.getId(), "User", "user@test.com",
                        "USER", true, LocalDateTime.now()));

        var result = userService.save(dto);

        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo("USER");
        then(userRepository).should().save(normalUser);
    }

    // ------------------------------------------------------------------ findById

    @Test
    @DisplayName("findById — existente → retorna DTO")
    void findById_existente_retornaDto() {
        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        given(mapper.toResponseDTO(adminUser)).willReturn(fakeResponse);

        var result = userService.findById(userId);

        assertThat(result.email()).isEqualTo("admin@test.com");
    }

    @Test
    @DisplayName("findById — inexistente → lança ResourceNotFoundException")
    void findById_inexistente_lancaException() {
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuário não encontrado.");
    }

    // ------------------------------------------------------------------ update

    @Test
    @DisplayName("update — email de outro usuário → lança BusinessRuleException")
    void update_emailDeOutroUsuario_lancaException() {
        var dto = new UserUpdateRequestDto("Nome", "outro@test.com");
        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        given(userRepository.existsByEmail("outro@test.com")).willReturn(true);

        assertThatThrownBy(() -> userService.update(userId, dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("E-mail já está em uso por outro usuário.");
    }

    @Test
    @DisplayName("update — mesmo email do próprio usuário → não lança exceção")
    void update_mesmoEmail_naoLancaException() {
        var dto = new UserUpdateRequestDto("Admin Renomeado", "admin@test.com");
        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        // email igual ao do próprio usuário: existsByEmail não deve ser chamado
        given(userRepository.save(adminUser)).willReturn(adminUser);
        given(mapper.toResponseDTO(adminUser)).willReturn(fakeResponse);

        assertThatNoException().isThrownBy(() -> userService.update(userId, dto));
    }

    // ------------------------------------------------------------------ updatePassword

    @Test
    @DisplayName("updatePassword — senha atual errada → lança BusinessRuleException")
    void updatePassword_senhaAtualErrada_lancaException() {
        var dto = new UserPasswordUpdateDto("senhaErrada", "novaSenha99", "novaSenha99");
        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        given(passwordEncoder.matches("senhaErrada", "hashed")).willReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(userId, dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Senha atual incorreta.");
    }

    @Test
    @DisplayName("updatePassword — confirmação não confere → lança BusinessRuleException")
    void updatePassword_confirmacaoDivergente_lancaException() {
        var dto = new UserPasswordUpdateDto("senha1234", "novaSenha99", "outraCoisa");
        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        given(passwordEncoder.matches("senha1234", "hashed")).willReturn(true);

        assertThatThrownBy(() -> userService.updatePassword(userId, dto))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Nova senha e confirmação não conferem.");
    }

    @Test
    @DisplayName("updatePassword — dados válidos → senha atualizada")
    void updatePassword_dadosValidos_atualizaSenha() {
        var dto = new UserPasswordUpdateDto("senha1234", "novaSenha99", "novaSenha99");
        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        given(passwordEncoder.matches("senha1234", "hashed")).willReturn(true);
        given(passwordEncoder.encode("novaSenha99")).willReturn("novoHash");
        given(userRepository.save(adminUser)).willReturn(adminUser);

        assertThatNoException().isThrownBy(() -> userService.updatePassword(userId, dto));
        assertThat(adminUser.getPassword()).isEqualTo("novoHash");
    }

    // ------------------------------------------------------------------ toggleActive

    @Test
    @DisplayName("toggleActive — ativo → desativa (inverte estado)")
    void toggleActive_ativo_desativa() {
        adminUser.setActive(true);
        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        given(userRepository.save(adminUser)).willReturn(adminUser);
        given(mapper.toResponseDTO(adminUser)).willReturn(
                new UserResponseDto(userId, "Admin", "admin@test.com", "ADMIN", false, LocalDateTime.now()));

        var result = userService.toggleActive(userId);

        assertThat(result.active()).isFalse();
        assertThat(adminUser.getActive()).isFalse();
    }

    @Test
    @DisplayName("toggleActive — inativo → ativa (inverte estado)")
    void toggleActive_inativo_ativa() {
        adminUser.setActive(false);
        given(userRepository.findById(userId)).willReturn(Optional.of(adminUser));
        given(userRepository.save(adminUser)).willReturn(adminUser);
        given(mapper.toResponseDTO(adminUser)).willReturn(
                new UserResponseDto(userId, "Admin", "admin@test.com", "ADMIN", true, LocalDateTime.now()));

        var result = userService.toggleActive(userId);

        assertThat(result.active()).isTrue();
        assertThat(adminUser.getActive()).isTrue();
    }
}
