package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloUnidad}.
 */
public interface ModeloUnidadService {

  /**
   * Guarda la entidad {@link ModeloUnidad}.
   * 
   * @param modeloUnidad la entidad {@link ModeloUnidad} a guardar.
   * @return la entidad {@link ModeloUnidad} persistida.
   */
  ModeloUnidad create(ModeloUnidad modeloUnidad);

  /**
   * Desactiva el {@link ModeloUnidad}.
   *
   * @param id Id del {@link ModeloUnidad}.
   * @return la entidad {@link ModeloUnidad} persistida.
   */
  ModeloUnidad disable(Long id);

  /**
   * Obtiene los {@link ModeloUnidad} activos.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link ModeloUnidad} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloUnidad> findAll(String query, Pageable pageable);

  /**
   * Obtiene una entidad {@link ModeloUnidad} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloUnidad}.
   * @return la entidad {@link ModeloUnidad}.
   */
  ModeloUnidad findById(final Long id);

  /**
   * Obtiene los {@link ModeloUnidad} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloUnidad} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloUnidad> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable);

}