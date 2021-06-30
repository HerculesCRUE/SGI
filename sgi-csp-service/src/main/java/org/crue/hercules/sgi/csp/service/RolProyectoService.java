package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link RolProyecto}.
 */

public interface RolProyectoService {

  /**
   * Obtiene una entidad {@link RolProyecto} por id.
   * 
   * @param id Identificador de la entidad {@link RolProyecto}.
   * @return RolProyecto la entidad {@link RolProyecto}.
   */
  RolProyecto findById(final Long id);

  /**
   * Obtiene todas las entidades {@link RolProyecto} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolProyecto} activas paginadas y
   *         filtradas.
   */
  Page<RolProyecto> findAll(String query, Pageable paging);
}
