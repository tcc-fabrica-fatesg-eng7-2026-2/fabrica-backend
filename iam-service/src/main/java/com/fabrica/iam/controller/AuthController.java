package com.fabrica.iam.controller;

import com.fabrica.iam.domain.Usuario;
import com.fabrica.iam.dto.AuthResponse;
import com.fabrica.iam.dto.LoginRequest;
import com.fabrica.iam.dto.RefreshTokenRequest;
import com.fabrica.iam.repository.UsuarioRepository;
import com.fabrica.iam.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(request.getLogin());
        
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenhaHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha inválida");
        }

        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        return ResponseEntity.ok(new AuthResponse(token, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        try {
            String login = jwtService.extractLogin(request.getRefreshToken());
            Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(login);
            
            if (usuarioOpt.isEmpty() || !jwtService.isTokenValid(request.getRefreshToken(), login)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido");
            }
            
            Usuario usuario = usuarioOpt.get();
            String newToken = jwtService.generateToken(usuario);
            String newRefreshToken = jwtService.generateRefreshToken(usuario);
            
            return ResponseEntity.ok(new AuthResponse(newToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado ou mal formatado");
        }
    }
}
