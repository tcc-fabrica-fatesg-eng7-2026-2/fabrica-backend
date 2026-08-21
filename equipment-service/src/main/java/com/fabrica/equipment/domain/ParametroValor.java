package com.fabrica.equipment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.util.UUID;

@Entity
public class ParametroValor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idValor;

    @ManyToOne
    @JoinColumn(name = "id_parametro", nullable = false)
    private ParametroMetadado parametro;

    private String idFilial; // "-1" para global
    private String valorParametro;

    public ParametroValor() {}

    public UUID getIdValor() { return idValor; }
    public void setIdValor(UUID idValor) { this.idValor = idValor; }
    public ParametroMetadado getParametro() { return parametro; }
    public void setParametro(ParametroMetadado parametro) { this.parametro = parametro; }
    public String getIdFilial() { return idFilial; }
    public void setIdFilial(String idFilial) { this.idFilial = idFilial; }
    public String getValorParametro() { return valorParametro; }
    public void setValorParametro(String valorParametro) { this.valorParametro = valorParametro; }
}
