package com.fabrica.equipment.repository;

import com.fabrica.equipment.domain.ParametroListaValores;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface ParametroListaValoresRepository extends JpaRepository<ParametroListaValores, UUID> {
    List<ParametroListaValores> findByParametroIdParametro(String idParametro);
}
