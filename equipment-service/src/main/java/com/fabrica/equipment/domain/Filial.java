package com.fabrica.equipment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idFilial;

    private String nome;
    private String cnpj;
    private String endereco;

    public Filial() {}

    public UUID getIdFilial() { return idFilial; }
    public void setIdFilial(UUID idFilial) { this.idFilial = idFilial; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
}
