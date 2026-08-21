package com.fabrica.equipment.repository;

import com.fabrica.equipment.domain.Instrumento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InstrumentoRepository extends JpaRepository<Instrumento, UUID> {
    Optional<Instrumento> findByTag(String tag);
}
