package com.fabrica.equipment.repository;

import com.fabrica.equipment.domain.ParametroValor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface ParametroValorRepository extends JpaRepository<ParametroValor, UUID> {
    List<ParametroValor> findByParametroIdParametroAndIdFilial(String idParametro, String idFilial);
}
