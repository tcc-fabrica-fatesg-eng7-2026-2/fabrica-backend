package com.fabrica.equipment.repository;

import com.fabrica.equipment.domain.Criticidade;
import com.fabrica.equipment.domain.Instrumento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class InstrumentoTest {

    @Autowired
    private InstrumentoRepository repo;

    @Test
    public void testPersistInstrumento() {
        Instrumento inst = new Instrumento();
        inst.setIdFilial("FIL-01");
        inst.setTag("TT-101");
        inst.setNome("Transmissor de Temperatura");
        inst.setRangeInicial(0.0);
        inst.setRangeFinal(100.0);
        inst.setUnidadeMedida("°C");
        inst.setCriticidade(Criticidade.PCC);
        inst.setAgendamentoAutomatico(true);

        Instrumento saved = repo.save(inst);
        assertNotNull(saved.getIdInstrumento());

        Instrumento found = repo.findByTag("TT-101").orElse(null);
        assertNotNull(found);
        assertEquals(Criticidade.PCC, found.getCriticidade());
        assertEquals(100.0, found.getRangeFinal());
    }
}
