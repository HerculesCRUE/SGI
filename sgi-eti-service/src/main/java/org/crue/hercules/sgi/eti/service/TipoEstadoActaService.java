package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.TipoEstadoActaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoEstadoActa}.
 */
public interface TipoEstadoActaService {

  /**
   * Guardar {@link TipoEstadoActa}.
   *
   * @param tipoEstadoActa la entidad {@link TipoEstadoActa} a guardar.
   * @return la entidad {@link TipoEstadoActa} persistida.
   */
  TipoEstadoActa create(TipoEstadoActa tipoEstadoActa);

  /**
   * Actualizar {@link TipoEstadoActa}.
   *
   * @param tipoEstadoActa la entidad {@link TipoEstadoActa} a actualizar.
   * @return la entidad {@link TipoEstadoActa} persistida.
   */
  TipoEstadoActa update(TipoEstadoActa tipoEstadoActa);

  /**
   * Obtener todas las entidades {@link TipoEstadoActa} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoEstadoActa} paginadas y/o filtradas.
   */
  Page<TipoEstadoActa> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoEstadoActa} por id.
   *
   * @param id el id de la entidad {@link TipoEstadoActa}.
   * @return la entidad {@link TipoEstadoActa}.
   */
  TipoEstadoActa findById(Long id);

  /**
   * Elimina el {@link TipoEstadoActa} por id.
   *
   * @param id el id de la entidad {@link TipoEstadoActa}.
   */
  void delete(Long id) throws TipoEstadoActaNotFoundException;

  /**
   * Elimina todos los {@link TipoEstadoActa}.
   */
  void deleteAll();

}