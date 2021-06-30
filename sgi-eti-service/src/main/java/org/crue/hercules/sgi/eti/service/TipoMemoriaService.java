package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.TipoMemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoMemoria}.
 */
public interface TipoMemoriaService {
  /**
   * Guardar {@link TipoMemoria}.
   *
   * @param tipoMemoria la entidad {@link TipoMemoria} a guardar.
   * @return la entidad {@link TipoMemoria} persistida.
   */
  TipoMemoria create(TipoMemoria tipoMemoria);

  /**
   * Actualizar {@link TipoMemoria}.
   *
   * @param tipoMemoria la entidad {@link TipoMemoria} a actualizar.
   * @return la entidad {@link TipoMemoria} persistida.
   */
  TipoMemoria update(TipoMemoria tipoMemoria);

  /**
   * Obtener todas las entidades {@link TipoMemoria} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoMemoria} paginadas y/o filtradas.
   */
  Page<TipoMemoria> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoMemoria} por id.
   *
   * @param id el id de la entidad {@link TipoMemoria}.
   * @return la entidad {@link TipoMemoria}.
   */
  TipoMemoria findById(Long id);

  /**
   * Elimina el {@link TipoMemoria} por id.
   *
   * @param id el id de la entidad {@link TipoMemoria}.
   */
  void delete(Long id) throws TipoMemoriaNotFoundException;

  /**
   * Elimina todos los {@link TipoMemoria}.
   */
  void deleteAll();

}