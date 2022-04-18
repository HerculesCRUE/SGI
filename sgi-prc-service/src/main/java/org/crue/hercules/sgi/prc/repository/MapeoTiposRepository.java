package org.crue.hercules.sgi.prc.repository;

import java.util.Optional;

import org.crue.hercules.sgi.prc.enums.CodigoCVN;
import org.crue.hercules.sgi.prc.model.MapeoTipos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link MapeoTipos}.
 */

@Repository
public interface MapeoTiposRepository
    extends JpaRepository<MapeoTipos, Long>, JpaSpecificationExecutor<MapeoTipos> {

  Optional<MapeoTipos> findByCodigoCVNAndIdTipoRef(CodigoCVN codigoCVN, Long idTipoRef);
}
