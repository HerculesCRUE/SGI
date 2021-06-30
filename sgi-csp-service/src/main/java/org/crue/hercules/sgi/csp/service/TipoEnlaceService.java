package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoEnlace}.
 */

public interface TipoEnlaceService {

  /**
   * Guarda la entidad {@link TipoEnlace}.
   * 
   * @param tipoEnlace la entidad {@link TipoEnlace} a guardar.
   * @return TipoEnlace la entidad {@link TipoEnlace} persistida.
   */
  TipoEnlace create(TipoEnlace tipoEnlace);

  /**
   * Actualiza los datos del {@link TipoEnlace}.
   * 
   * @param tipoEnlaceActualizar {@link TipoEnlace} con los datos actualizados.
   * @return TipoEnlace {@link TipoEnlace} actualizado.
   */
  TipoEnlace update(final TipoEnlace tipoEnlaceActualizar);

  /**
   * Reactiva el {@link TipoEnlace}.
   *
   * @param id Id del {@link TipoEnlace}.
   * @return la entidad {@link TipoEnlace} persistida.
   */
  TipoEnlace enable(Long id);

  /**
   * Desactiva el {@link TipoEnlace}.
   *
   * @param id Id del {@link TipoEnlace}.
   * @return la entidad {@link TipoEnlace} persistida.
   */
  TipoEnlace disable(Long id);

  /**
   * Obtiene todas las entidades {@link TipoEnlace} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  Page<TipoEnlace> findAll(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link TipoEnlace} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  Page<TipoEnlace> findAllTodos(String query, Pageable paging);

  /**
   * Obtiene una entidad {@link TipoEnlace} por id.
   * 
   * @param id Identificador de la entidad {@link TipoEnlace}.
   * @return TipoEnlace la entidad {@link TipoEnlace}.
   */
  TipoEnlace findById(final Long id);

}
