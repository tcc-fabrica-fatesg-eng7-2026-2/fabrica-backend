package com.fabrica.equipment.repository;

import com.fabrica.equipment.domain.ParametroMetadado;
import com.fabrica.equipment.domain.ParametroValor;
import com.fabrica.equipment.domain.ParametroListaValores;
import com.fabrica.equipment.domain.TipoValor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ParametrosTest {

    @Autowired
    private ParametroMetadadoRepository metadadoRepo;

    @Autowired
    private ParametroValorRepository valorRepo;

    @Autowired
    private ParametroListaValoresRepository listaRepo;

    @Test
    public void testCriarParametroLista() {
        ParametroMetadado meta = new ParametroMetadado();
        meta.setIdParametro("TIPO_CERTIFICADO");
        meta.setDescricao("Tipo de certificado aceito");
        meta.setPorFilial(true);
        meta.setTipoValor(TipoValor.LISTA);
        meta = metadadoRepo.save(meta);

        ParametroListaValores val1 = new ParametroListaValores();
        val1.setParametro(meta);
        val1.setDescricao("RBC Padrão");
        val1.setValorArmazenado("RBC");
        listaRepo.save(val1);

        ParametroListaValores val2 = new ParametroListaValores();
        val2.setParametro(meta);
        val2.setDescricao("Interno");
        val2.setValorArmazenado("INT");
        listaRepo.save(val2);

        ParametroValor valFilial = new ParametroValor();
        valFilial.setParametro(meta);
        valFilial.setIdFilial("123");
        valFilial.setValorParametro("RBC"); // Escolheu RBC pra essa filial
        valorRepo.save(valFilial);

        List<ParametroListaValores> lista = listaRepo.findByParametroIdParametro("TIPO_CERTIFICADO");
        assertEquals(2, lista.size());

        List<ParametroValor> escolhas = valorRepo.findByParametroIdParametroAndIdFilial("TIPO_CERTIFICADO", "123");
        assertEquals(1, escolhas.size());
        assertEquals("RBC", escolhas.get(0).getValorParametro());
    }
}
