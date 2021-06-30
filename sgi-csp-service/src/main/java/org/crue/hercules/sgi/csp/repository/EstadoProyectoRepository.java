package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EstadoProyectoRepository
    extends JpaRepository<EstadoProyecto, Long>, JpaSpecificationExecutor<EstadoProyecto> {

  /**
   * Obtiene las {@link EstadoProyecto} para una {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link EstadoProyecto} de la {@link Proyecto}
   *         paginadas.
   */
  Page<EstadoProyecto> findAllByProyectoId(Long idProyecto, Pageable paging);

}
