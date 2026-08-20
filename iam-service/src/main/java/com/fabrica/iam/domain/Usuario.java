package com.fabrica.iam.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idUsuario;
    
    private String idFilial; // String to support '-1' global indicator, or UUID. Using String for flexibility.
    
    @Column(unique = true, nullable = false)
    private String login;
    
    @Column(nullable = false)
    private String senhaHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Getters and Setters
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }
    public String getIdFilial() { return idFilial; }
    public void setIdFilial(String idFilial) { this.idFilial = idFilial; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
