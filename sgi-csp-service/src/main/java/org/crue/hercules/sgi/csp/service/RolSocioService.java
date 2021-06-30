package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.RolSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link RolSocio}.
 */

public interface RolSocioService {

  /**
   * Obtiene una entidad {@link RolSocio} por id.
   * 
   * @param id Identificador de la entidad {@link RolSocio}.
   * @return RolSocio la entidad {@link RolSocio}.
   */
  RolSocio findById(final Long id);

  /**
   * Obtiene todas las entidades {@link RolSocio} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolSocio} activas paginadas y
   *         filtradas.
   */
  Page<RolSocio> findAll(String query, Pageable paging);
}
