package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoAmbitoGeografico}.
 */
public interface TipoAmbitoGeograficoService {

  /**
   * Obtener todas las entidades {@link TipoAmbitoGeografico} activos paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoAmbitoGeografico} paginadas y/o
   *         filtradas.
   */
  Page<TipoAmbitoGeografico> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoAmbitoGeografico} por su id.
   *
   * @param id el id de la entidad {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico}.
   */
  TipoAmbitoGeografico findById(Long id);

}