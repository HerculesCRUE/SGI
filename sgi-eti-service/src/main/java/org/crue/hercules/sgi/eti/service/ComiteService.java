package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.Comite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Comite}.
 */
public interface ComiteService {
  /**
   * Obtener todas las entidades {@link Comite} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Comite} paginadas y/o filtradas.
   */
  Page<Comite> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link Comite} por id.
   *
   * @param id el id de la entidad {@link Comite}.
   * @return la entidad {@link Comite}.
   */
  Comite findById(Long id);
}