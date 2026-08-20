package com.fabrica.iam.repository;

import com.fabrica.iam.domain.Role;
import com.fabrica.iam.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    public void testAdminUserIsSeeded() {
        Usuario admin = repository.findByLogin("admin").orElse(null);
        assertNotNull(admin, "O usuario admin deveria ser criado no startup pelo DataSeeder");
        assertEquals(Role.ADM, admin.getRole(), "O usuario admin deveria ter a role ADM");
        assertEquals("-1", admin.getIdFilial(), "O admin global deve ter filial -1");
    }
}
