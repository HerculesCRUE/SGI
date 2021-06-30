package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoFinalidad}.
 */

public interface TipoFinalidadService {

  /**
   * Guarda la entidad {@link TipoFinalidad}.
   * 
   * @param tipoFinalidad la entidad {@link TipoFinalidad} a guardar.
   * @return TipoFinalidad la entidad {@link TipoFinalidad} persistida.
   */
  TipoFinalidad create(TipoFinalidad tipoFinalidad);

  /**
   * Actualiza los datos del {@link TipoFinalidad}.
   * 
   * @param tipoFinalidadActualizar {@link TipoFinalidad} con los datos
   *                                actualizados.
   * @return TipoFinalidad {@link TipoFinalidad} actualizado.
   */
  TipoFinalidad update(final TipoFinalidad tipoFinalidadActualizar);

  /**
   * Reactiva el {@link TipoFinalidad}.
   *
   * @param id Id del {@link TipoFinalidad}.
   * @return la entidad {@link TipoFinalidad} persistida.
   */
  TipoFinalidad enable(Long id);

  /**
   * Desactiva el {@link TipoFinalidad}.
   *
   * @param id Id del {@link TipoFinalidad}.
   * @return la entidad {@link TipoFinalidad} persistida.
   */
  TipoFinalidad disable(Long id);

  /**
   * Obtiene todas las entidades {@link TipoFinalidad} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoFinalidad} paginadas y filtradas.
   */
  Page<TipoFinalidad> findAll(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link TipoFinalidad} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoFinalidad} paginadas y filtradas.
   */
  Page<TipoFinalidad> findAllTodos(String query, Pageable paging);

  /**
   * Obtiene una entidad {@link TipoFinalidad} por id.
   * 
   * @param id Identificador de la entidad {@link TipoFinalidad}.
   * @return TipoFinalidad la entidad {@link TipoFinalidad}.
   */
  TipoFinalidad findById(final Long id);

}
