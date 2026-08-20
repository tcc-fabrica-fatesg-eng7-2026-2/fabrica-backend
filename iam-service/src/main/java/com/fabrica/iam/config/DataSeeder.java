package com.fabrica.iam.config;

import com.fabrica.iam.domain.Role;
import com.fabrica.iam.domain.Usuario;
import com.fabrica.iam.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public DataSeeder(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (repo.findByLogin("admin").isEmpty()) {
            Usuario u = new Usuario();
            u.setLogin("admin");
            u.setSenhaHash(encoder.encode("admin"));
            u.setRole(Role.ADM);
            u.setIdFilial("-1"); // admin global
            repo.save(u);
        }
    }
}
