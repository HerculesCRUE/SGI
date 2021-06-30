package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.EstadoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link EstadoMemoria}.
 */
public interface EstadoMemoriaService {
  /**
   * Guardar {@link EstadoMemoria}.
   *
   * @param estadoMemoria la entidad {@link EstadoMemoria} a guardar.
   * @return la entidad {@link EstadoMemoria} persistida.
   */
  EstadoMemoria create(EstadoMemoria estadoMemoria);

  /**
   * Actualizar {@link EstadoMemoria}.
   *
   * @param estadoMemoria la entidad {@link EstadoMemoria} a actualizar.
   * @return la entidad {@link EstadoMemoria} persistida.
   */
  EstadoMemoria update(EstadoMemoria estadoMemoria);

  /**
   * Obtener todas las entidades {@link EstadoMemoria} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link EstadoMemoria} paginadas y/o filtradas.
   */
  Page<EstadoMemoria> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link EstadoMemoria} por id.
   *
   * @param id el id de la entidad {@link EstadoMemoria}.
   * @return la entidad {@link EstadoMemoria}.
   */
  EstadoMemoria findById(Long id);

  /**
   * Elimina el {@link EstadoMemoria} por id.
   *
   * @param id el id de la entidad {@link EstadoMemoria}.
   */
  void delete(Long id) throws EstadoMemoriaNotFoundException;

  /**
   * Elimina todos los {@link EstadoMemoria}.
   */
  void deleteAll();

}