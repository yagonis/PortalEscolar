package com.portalescolar.auth.security;

import com.portalescolar.config.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. pega o header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. se não tem token ou não começa com Bearer, deixa passar
        // rotas públicas vão funcionar normalmente
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. extrai só o token (remove "Bearer ")
        String token = authHeader.substring(7);

        try {
            // 4. extrai o email do token
            String email = jwtTokenProvider.extractEmail(token);

            // 5. se tem email e não tem ninguém autenticado ainda no contexto
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. carrega o usuário do banco
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 7. valida o token contra o usuário carregado
                if (jwtTokenProvider.isTokenValid(token, userDetails)) {

                    // 8. cria o objeto de autenticação com as permissões do usuário
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9. registra a autenticação no contexto do Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // token inválido ou expirado — deixa passar sem autenticar
            // o Spring Security vai barrar na rota se ela precisar de auth
        }

        filterChain.doFilter(request, response);
    }


}
