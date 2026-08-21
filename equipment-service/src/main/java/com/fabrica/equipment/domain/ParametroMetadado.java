package com.fabrica.equipment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class ParametroMetadado {

    @Id
    private String idParametro; // Ex: TIPO_CERTIFICADO

    private String descricao;
    private boolean porFilial;

    @Enumerated(EnumType.STRING)
    private TipoValor tipoValor;

    public ParametroMetadado() {}

    public String getIdParametro() { return idParametro; }
    public void setIdParametro(String idParametro) { this.idParametro = idParametro; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isPorFilial() { return porFilial; }
    public void setPorFilial(boolean porFilial) { this.porFilial = porFilial; }
    public TipoValor getTipoValor() { return tipoValor; }
    public void setTipoValor(TipoValor tipoValor) { this.tipoValor = tipoValor; }
}
