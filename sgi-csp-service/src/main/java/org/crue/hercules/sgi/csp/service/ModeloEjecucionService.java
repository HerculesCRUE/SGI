package org.crue.hercules.sgi.csp.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

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
  @Validated({ BaseEntity.Create.class })
  ModeloEjecucion create(@Valid ModeloEjecucion modeloEjecucion);

  /**
   * Actualizar {@link ModeloEjecucion}.
   *
   * @param modeloEjecucionActualizar la entidad {@link ModeloEjecucion} a
   *                                  actualizar.
   * @return la entidad {@link ModeloEjecucion} persistida.
   */
  @Validated({ BaseEntity.Update.class })
  ModeloEjecucion update(@Valid ModeloEjecucion modeloEjecucionActualizar);

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
   * @param query la información del filtro.
   * @return la lista de entidades {@link ModeloEjecucion} paginadas y/o
   *         filtradas.
   */
  List<ModeloEjecucion> findAll(String query);

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

  /**
   * Obtiene un Booleano indicando si el modelo de ejecucion esta asociado a algun
   * proyecto
   *
   * @param id el id de la entidad {@link ModeloEjecucion}.
   * @return true si existe algun proyecto asociado y false si no tiene ninguno.
   */
  Boolean hasProyectosAsociados(Long id);

  /**
   * Comprueba si existen o no {@link ModeloEjecucion} que cumplan con el filtro.
   *
   * @param query la información del filtro.
   * @return Si existen o no {@link ModeloEjecucion} que cumplan con el filtro.
   */
  boolean exists(String query);

}