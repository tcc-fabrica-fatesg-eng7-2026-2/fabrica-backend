package com.fabrica.equipment.repository;

import com.fabrica.equipment.domain.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FilialRepository extends JpaRepository<Filial, UUID> {
}
