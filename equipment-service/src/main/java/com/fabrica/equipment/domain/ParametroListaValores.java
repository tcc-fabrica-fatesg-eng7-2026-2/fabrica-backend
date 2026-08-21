package com.fabrica.equipment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.util.UUID;

@Entity
public class ParametroListaValores {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idListaValor;

    @ManyToOne
    @JoinColumn(name = "id_parametro", nullable = false)
    private ParametroMetadado parametro;

    private String descricao;
    private String valorArmazenado;

    public ParametroListaValores() {}

    public UUID getIdListaValor() { return idListaValor; }
    public void setIdListaValor(UUID idListaValor) { this.idListaValor = idListaValor; }
    public ParametroMetadado getParametro() { return parametro; }
    public void setParametro(ParametroMetadado parametro) { this.parametro = parametro; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getValorArmazenado() { return valorArmazenado; }
    public void setValorArmazenado(String valorArmazenado) { this.valorArmazenado = valorArmazenado; }
}
