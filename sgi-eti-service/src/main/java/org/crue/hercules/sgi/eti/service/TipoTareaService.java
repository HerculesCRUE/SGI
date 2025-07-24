package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoTarea}.
 */
public interface TipoTareaService {

  /**
   * Obtener todas las entidades {@link TipoTarea} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoTarea} paginadas y/o filtradas.
   */
  Page<TipoTarea> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoTarea} por id.
   *
   * @param id el id de la entidad {@link TipoTarea}.
   * @return la entidad {@link TipoTarea}.
   */
  TipoTarea findById(Long id);
}