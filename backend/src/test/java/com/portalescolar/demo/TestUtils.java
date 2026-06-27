package com.portalescolar.demo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portalescolar.user.entity.Role;
import com.portalescolar.user.entity.User;
import com.portalescolar.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
public class TestUtils {
    public static final String ADMIN_EMAIL    = "admin@teste.com";
    public static final String ADMIN_PASSWORD = "senha1234";
    public static final String USER_EMAIL     = "user@teste.com";
    public static final String USER_PASSWORD  = "senha1234";

    /** Insere um ADMIN no banco e retorna a entidade salva. */
    public static User createAdmin(UserRepository repo, PasswordEncoder encoder) {
        if (repo.findByEmail(ADMIN_EMAIL).isPresent()) {
            return repo.findByEmail(ADMIN_EMAIL).get();
        }
        User admin = User.builder()
                .name("Admin Teste")
                .email(ADMIN_EMAIL)
                .password(encoder.encode(ADMIN_PASSWORD))
                .role(Role.ADMIN)
                .active(true)
                .build();
        return repo.save(admin);
    }

    /** Insere um USER comum no banco e retorna a entidade salva. */
    public static User createUser(UserRepository repo, PasswordEncoder encoder) {
        if (repo.findByEmail(USER_EMAIL).isPresent()) {
            return repo.findByEmail(USER_EMAIL).get();
        }
        User user = User.builder()
                .name("Usuário Teste")
                .email(USER_EMAIL)
                .password(encoder.encode(USER_PASSWORD))
                .role(Role.USER)
                .active(true)
                .build();
        return repo.save(user);
    }

    /** Insere um USER com email customizado. */
    public static User createUserWithEmail(UserRepository repo, PasswordEncoder encoder,
                                           String name, String email) {
        if (repo.findByEmail(email).isPresent()) {
            return repo.findByEmail(email).get();
        }
        User user = User.builder()
                .name(name)
                .email(email)
                .password(encoder.encode("senha1234"))
                .role(Role.USER)
                .active(true)
                .build();
        return repo.save(user);
    }

    /** Serializa um objeto para JSON string. */
    public static String toJson(Object obj, ObjectMapper mapper) throws Exception {
        return mapper.writeValueAsString(obj);
    }
}
