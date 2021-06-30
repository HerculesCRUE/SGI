package org.crue.hercules.sgi.eti.service;

import org.crue.hercules.sgi.eti.exceptions.TipoComentarioNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoComentario}.
 */
public interface TipoComentarioService {
  /**
   * Guardar {@link TipoComentario}.
   *
   * @param tipoComentario la entidad {@link TipoComentario} a guardar.
   * @return la entidad {@link TipoComentario} persistida.
   */
  TipoComentario create(TipoComentario tipoComentario);

  /**
   * Actualizar {@link TipoComentario}.
   *
   * @param tipoComentario la entidad {@link TipoComentario} a actualizar.
   * @return la entidad {@link TipoComentario} persistida.
   */
  TipoComentario update(TipoComentario tipoComentario);

  /**
   * Obtener todas las entidades {@link TipoComentario} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoComentario} paginadas y/o filtradas.
   */
  Page<TipoComentario> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoComentario} por id.
   *
   * @param id el id de la entidad {@link TipoComentario}.
   * @return la entidad {@link TipoComentario}.
   */
  TipoComentario findById(Long id);

  /**
   * Elimina el {@link TipoComentario} por id.
   *
   * @param id el id de la entidad {@link TipoComentario}.
   */
  void delete(Long id) throws TipoComentarioNotFoundException;

  /**
   * Elimina todos los {@link TipoComentario}.
   */
  void deleteAll();

}