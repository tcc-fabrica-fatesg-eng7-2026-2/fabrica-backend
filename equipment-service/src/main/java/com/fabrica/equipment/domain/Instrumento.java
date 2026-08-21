package com.fabrica.equipment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Instrumento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idInstrumento;

    @Column(nullable = false)
    private String idFilial;

    @Column(unique = true, nullable = false)
    private String tag;

    private String corEtiqueta;
    private String nome;
    private String area;
    private String localizacao;
    private String idEquipamentoPai; // Pode ser mapeado no futuro
    
    @Column(length = 1000)
    private String descricao;
    
    private Double rangeInicial;
    private Double rangeFinal;
    private String unidadeMedida;
    
    private Double faixaUsoInicial;
    private Double faixaUsoFinal;
    
    @Enumerated(EnumType.STRING)
    private Criticidade criticidade;
    
    private boolean equipamentoCritico;
    
    private String numeroSap;
    private String numeroPlano;
    
    private boolean agendamentoAutomatico;

    public Instrumento() {}

    // Getters and Setters
    public UUID getIdInstrumento() { return idInstrumento; }
    public void setIdInstrumento(UUID idInstrumento) { this.idInstrumento = idInstrumento; }
    public String getIdFilial() { return idFilial; }
    public void setIdFilial(String idFilial) { this.idFilial = idFilial; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getCorEtiqueta() { return corEtiqueta; }
    public void setCorEtiqueta(String corEtiqueta) { this.corEtiqueta = corEtiqueta; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public String getIdEquipamentoPai() { return idEquipamentoPai; }
    public void setIdEquipamentoPai(String idEquipamentoPai) { this.idEquipamentoPai = idEquipamentoPai; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Double getRangeInicial() { return rangeInicial; }
    public void setRangeInicial(Double rangeInicial) { this.rangeInicial = rangeInicial; }
    public Double getRangeFinal() { return rangeFinal; }
    public void setRangeFinal(Double rangeFinal) { this.rangeFinal = rangeFinal; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
    public Double getFaixaUsoInicial() { return faixaUsoInicial; }
    public void setFaixaUsoInicial(Double faixaUsoInicial) { this.faixaUsoInicial = faixaUsoInicial; }
    public Double getFaixaUsoFinal() { return faixaUsoFinal; }
    public void setFaixaUsoFinal(Double faixaUsoFinal) { this.faixaUsoFinal = faixaUsoFinal; }
    public Criticidade getCriticidade() { return criticidade; }
    public void setCriticidade(Criticidade criticidade) { this.criticidade = criticidade; }
    public boolean isEquipamentoCritico() { return equipamentoCritico; }
    public void setEquipamentoCritico(boolean equipamentoCritico) { this.equipamentoCritico = equipamentoCritico; }
    public String getNumeroSap() { return numeroSap; }
    public void setNumeroSap(String numeroSap) { this.numeroSap = numeroSap; }
    public String getNumeroPlano() { return numeroPlano; }
    public void setNumeroPlano(String numeroPlano) { this.numeroPlano = numeroPlano; }
    public boolean isAgendamentoAutomatico() { return agendamentoAutomatico; }
    public void setAgendamentoAutomatico(boolean agendamentoAutomatico) { this.agendamentoAutomatico = agendamentoAutomatico; }
}
