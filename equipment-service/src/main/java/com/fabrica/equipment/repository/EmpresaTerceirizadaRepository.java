package com.fabrica.equipment.repository;

import com.fabrica.equipment.domain.EmpresaTerceirizada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmpresaTerceirizadaRepository extends JpaRepository<EmpresaTerceirizada, UUID> {
}
