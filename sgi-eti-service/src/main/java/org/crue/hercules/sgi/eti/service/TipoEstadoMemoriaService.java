package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.TipoEstadoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoEstadoMemoria}.
 */
public interface TipoEstadoMemoriaService {
  /**
   * Guardar {@link TipoEstadoMemoria}.
   *
   * @param tipoEstadoMemoria la entidad {@link TipoEstadoMemoria} a guardar.
   * @return la entidad {@link TipoEstadoMemoria} persistida.
   */
  TipoEstadoMemoria create(TipoEstadoMemoria tipoEstadoMemoria);

  /**
   * Actualizar {@link TipoEstadoMemoria}.
   *
   * @param tipoEstadoMemoria la entidad {@link TipoEstadoMemoria} a actualizar.
   * @return la entidad {@link TipoEstadoMemoria} persistida.
   */
  TipoEstadoMemoria update(TipoEstadoMemoria tipoEstadoMemoria);

  /**
   * Obtener todas las entidades {@link TipoEstadoMemoria} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoEstadoMemoria} paginadas y/o
   *         filtradas.
   */
  Page<TipoEstadoMemoria> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoEstadoMemoria} por id.
   *
   * @param id el id de la entidad {@link TipoEstadoMemoria}.
   * @return la entidad {@link TipoEstadoMemoria}.
   */
  TipoEstadoMemoria findById(Long id);

  /**
   * Elimina el {@link TipoEstadoMemoria} por id.
   *
   * @param id el id de la entidad {@link TipoEstadoMemoria}.
   */
  void delete(Long id) throws TipoEstadoMemoriaNotFoundException;

  /**
   * Elimina todos los {@link TipoEstadoMemoria}.
   */
  void deleteAll();

}