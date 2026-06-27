package com.portalescolar.auth.service;

import com.portalescolar.auth.dto.LoginRequestDto;
import com.portalescolar.auth.dto.LoginResponseDto;
import com.portalescolar.auth.security.JwtTokenProvider;
import com.portalescolar.shared.exception.BusinessRuleException;
import com.portalescolar.user.entity.User;
import com.portalescolar.user.mapper.UserMapper;
import com.portalescolar.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import org.springframework.security.core.AuthenticationException;

@AllArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public LoginResponseDto login(LoginRequestDto dto) {
        try {
            // 1. autentica email e senha
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
            );
        } catch (DisabledException e) {
            // Captura PRIMEIRO o erro de inatividade gerado pelo UserDetails
            throw new BusinessRuleException("Usuário desativado. Entre em contato com o administrador.");
        } catch (AuthenticationException e) {
            // Captura senha errada e outros problemas genéricos de autenticação
            throw new BusinessRuleException("Email ou senha inválidos.");
        }

        // 2. busca o usuário do banco
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BusinessRuleException("Usuário não encontrado."));

        // 3. verifica se está ativo (Opcional)
        // Nota: Como o Spring Security já lança a DisabledException lá no Passo 1
        // (se o seu UserDetails configurou isEnabled corretamente),
        // esse passo 3 acaba virando apenas uma camada de segurança extra.
        if (!user.getActive()) {
            throw new BusinessRuleException("Usuário desativado. Entre em contato com o administrador.");
        }

        // 4. gera o token
        String token = jwtTokenProvider.generateToken(user);

        // 5. monta e retorna o response
        return new LoginResponseDto(
                token,
                "Bearer",
                jwtTokenProvider.extractExpiration(token),
                userMapper.toResponseDTO(user)
        );
    }
}
