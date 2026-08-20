package com.fabrica.iam.security;

import com.fabrica.iam.domain.Role;
import com.fabrica.iam.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        // Usando uma string HEX fake de 64 bytes para evitar erro de WeakKeyException no HMAC-SHA256
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hora
    }

    @Test
    public void testTokenGenerationAndValidation() {
        Usuario usuario = new Usuario();
        usuario.setLogin("joao.instrumentista");
        usuario.setRole(Role.INSTRUMENTISTA);
        usuario.setIdFilial("1234");

        String token = jwtService.generateToken(usuario);
        assertNotNull(token);

        String login = jwtService.extractLogin(token);
        assertEquals("joao.instrumentista", login);

        assertTrue(jwtService.isTokenValid(token, usuario.getLogin()));
    }
}
