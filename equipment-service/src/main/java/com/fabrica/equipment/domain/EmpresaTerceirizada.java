package com.fabrica.equipment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class EmpresaTerceirizada {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idTerceirizada;

    private String cnpj;
    private String nome;

    public EmpresaTerceirizada() {}

    public UUID getIdTerceirizada() { return idTerceirizada; }
    public void setIdTerceirizada(UUID idTerceirizada) { this.idTerceirizada = idTerceirizada; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
