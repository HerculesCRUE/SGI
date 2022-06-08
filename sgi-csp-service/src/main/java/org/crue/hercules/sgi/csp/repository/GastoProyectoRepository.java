package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link GastoProyecto}.
 */
@Repository
public interface GastoProyectoRepository
    extends JpaRepository<GastoProyecto, Long>, JpaSpecificationExecutor<GastoProyecto> {

  /**
   * Indica si existen {@link GastoProyecto} de un {@link Proyecto}
   * 
   * @param proyectoId identificador de la {@link Proyecto}
   * @return si existen {@link GastoProyecto} asociados al {@link Proyecto}
   */
  boolean existsByProyectoId(Long proyectoId);

}
