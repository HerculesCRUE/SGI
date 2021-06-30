package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.EstadoActaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link EstadoActa}.
 */
public interface EstadoActaService {

  /**
   * Guardar {@link EstadoActa}.
   *
   * @param estadoActa la entidad {@link EstadoActa} a guardar.
   * @return la entidad {@link EstadoActa} persistida.
   */
  EstadoActa create(EstadoActa estadoActa);

  /**
   * Actualizar {@link EstadoActa}.
   *
   * @param estadoActa la entidad {@link EstadoActa} a actualizar.
   * @return la entidad {@link EstadoActa} persistida.
   */
  EstadoActa update(EstadoActa estadoActa);

  /**
   * Obtener todas las entidades {@link EstadoActa} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link EstadoActa} paginadas y/o filtradas.
   */
  Page<EstadoActa> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link EstadoActa} por id.
   *
   * @param id el id de la entidad {@link EstadoActa}.
   * @return la entidad {@link EstadoActa}.
   */
  EstadoActa findById(Long id);

  /**
   * Elimina el {@link EstadoActa} por id.
   *
   * @param id el id de la entidad {@link EstadoActa}.
   */
  void delete(Long id) throws EstadoActaNotFoundException;

}