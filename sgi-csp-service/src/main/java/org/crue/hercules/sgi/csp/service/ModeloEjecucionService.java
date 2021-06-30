package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloEjecucion}.
 */
public interface ModeloEjecucionService {

  /**
   * Guardar un nuevo {@link ModeloEjecucion}.
   *
   * @param modeloEjecucion la entidad {@link ModeloEjecucion} a guardar.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  ModeloEjecucion create(ModeloEjecucion modeloEjecucion);

  /**
   * Actualizar {@link ModeloEjecucion}.
   *
   * @param modeloEjecucionActualizar la entidad {@link ModeloEjecucion} a
   *                                  actualizar.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  ModeloEjecucion update(ModeloEjecucion modeloEjecucionActualizar);

  /**
   * Reactiva el {@link ModeloEjecucion}.
   *
   * @param id Id del {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  ModeloEjecucion enable(Long id);

  /**
   * Desactiva el {@link ModeloEjecucion}.
   *
   * @param id Id del {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  ModeloEjecucion disable(Long id);

  /**
   * Obtener todas las entidades {@link ModeloEjecucion} activas paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ModeloEjecucion} paginadas y/o
   *         filtradas.
   */
  Page<ModeloEjecucion> findAll(String query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link ModeloEjecucion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ModeloEjecucion} paginadas y/o
   *         filtradas.
   */
  Page<ModeloEjecucion> findAllTodos(String query, Pageable pageable);

  /**
   * Obtiene {@link ModeloEjecucion} por su id.
   *
   * @param id el id de la entidad {@link ModeloEjecucion}.
   * @return la entidad {@link ModeloEjecucion}.
   */
  ModeloEjecucion findById(Long id);

}