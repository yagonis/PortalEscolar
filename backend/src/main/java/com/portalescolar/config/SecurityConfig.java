package com.portalescolar.config;

import com.portalescolar.auth.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver exceptionResolver;

    public SecurityConfig(
            UserDetailsServiceImpl userDetailsService,
            JwtAuthFilter jwtAuthFilter,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {

        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                exceptionResolver.resolveException(request, response, null, authException)
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                exceptionResolver.resolveException(request, response, null, accessDeniedException)
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // votar — precisa estar autenticado
                        .requestMatchers(HttpMethod.POST, "/api/polls/{id}/vote").authenticated()

                        // --- PÚBLICO --- nenhum token necessário
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()


                        // notícias: GET público (o service já filtra só PUBLISHED para não autenticados)
                        .requestMatchers(HttpMethod.GET, "/api/news").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/news/{id}").permitAll()

                        // avisos: GET público (o service já filtra só ativos)
                        .requestMatchers(HttpMethod.GET, "/api/warnings").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/warnings/{id}").permitAll()

                        // polls — GET público
                        .requestMatchers(HttpMethod.GET, "/api/polls").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/polls/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/polls/{id}/result").permitAll()

                        // --- APENAS ADMIN ---
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/warnings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/warnings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/warnings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/warnings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/news").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/polls").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/polls/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/polls/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/polls/**").hasRole("ADMIN")

                        // qualquer outra rota precisa estar autenticado
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                // registra o filtro JWT ANTES do filtro padrão de autenticação
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
