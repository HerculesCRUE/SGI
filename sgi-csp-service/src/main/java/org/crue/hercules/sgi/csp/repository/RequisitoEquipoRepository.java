package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link RequisitoEquipo}.
 */
@Repository
public interface RequisitoEquipoRepository
    extends JpaRepository<RequisitoEquipo, Long>, JpaSpecificationExecutor<RequisitoEquipo> {

  /**
   * Obtiene el {@link RequisitoEquipo} asociado a la {@link Convocatoria}
   * 
   * @param convocatoriaId id de la {@link Convocatoria}
   * @return {@link RequisitoEquipo}
   */
  Optional<RequisitoEquipo> findByConvocatoriaId(Long convocatoriaId);
}
