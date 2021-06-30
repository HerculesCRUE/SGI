package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContextoProyectoRepository
    extends JpaRepository<ContextoProyecto, Long>, JpaSpecificationExecutor<ContextoProyecto> {

  /**
   * Obtiene el {@link ContextoProyecto} de un {@link Proyecto}
   * 
   * @param id identificador de la {@link Proyecto}
   * @return la entidad {@link ContextoProyecto}
   */
  Optional<ContextoProyecto> findByProyectoId(Long id);

  /**
   * Indica si existe el {@link ContextoProyecto} de un {@link Proyecto}
   * 
   * @param id identificador de la {@link Proyecto}
   * @return si existe la entidad {@link ContextoProyecto}
   */
  boolean existsByProyectoId(Long id);

}
