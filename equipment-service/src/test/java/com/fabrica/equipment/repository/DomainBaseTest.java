package com.fabrica.equipment.repository;

import com.fabrica.equipment.domain.EmpresaTerceirizada;
import com.fabrica.equipment.domain.Filial;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DomainBaseTest {

    @Autowired
    private FilialRepository filialRepo;

    @Autowired
    private EmpresaTerceirizadaRepository empresaRepo;

    @Test
    public void testPersistFilial() {
        Filial filial = new Filial();
        filial.setNome("Fábrica Matriz");
        filial.setCnpj("11.222.333/0001-44");
        
        Filial saved = filialRepo.save(filial);
        assertNotNull(saved.getIdFilial());
        
        Filial found = filialRepo.findById(saved.getIdFilial()).orElse(null);
        assertNotNull(found);
        assertEquals("Fábrica Matriz", found.getNome());
    }

    @Test
    public void testPersistEmpresa() {
        EmpresaTerceirizada emp = new EmpresaTerceirizada();
        emp.setNome("CalibraTech");
        
        EmpresaTerceirizada saved = empresaRepo.save(emp);
        assertNotNull(saved.getIdTerceirizada());
    }
}
