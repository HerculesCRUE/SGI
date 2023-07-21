package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

/**
 * Service Interface para gestionar {@link RolProyecto}.
 */
@Validated
public interface RolProyectoService {

  /**
   * Guardar un nuevo {@link RolProyecto}.
   *
   * @param rolProyecto la entidad {@link RolProyecto} a guardar.
   * @return la entidad {@link RolProyecto} persistida.
   */
  RolProyecto create(@Valid RolProyecto rolProyecto);

  /**
   * Actualizar {@link RolProyecto}.
   *
   * @param rolProyectoActualizar la entidad {@link RolProyecto} a actualizar.
   * @return la entidad {@link RolProyecto} persistida.
   */
  RolProyecto update(@Valid RolProyecto rolProyectoActualizar);

  /**
   * Obtiene una entidad {@link RolProyecto} por id.
   * 
   * @param id Identificador de la entidad {@link RolProyecto}.
   * @return RolProyecto la entidad {@link RolProyecto}.
   */
  RolProyecto findById(final Long id);

  /**
   * Obtiene una entidad {@link RolProyecto} por orden Principal.
   * 
   * @return RolProyecto la entidad {@link RolProyecto}.
   */
  RolProyecto findPrincipal();

  /**
   * Devuelve si existe una entidad {@link RolProyecto} por orden Principal.
   * 
   * @return true or false
   */
  boolean existsPrincipal();

  /**
   * Obtiene todas las entidades {@link RolProyecto} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolProyecto} activas paginadas y
   *         filtradas.
   */
  Page<RolProyecto> findAll(String query, Pageable paging);

  /**
   * Obtener todas las entidades {@link RolProyecto} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link RolProyecto} paginadas y/o filtradas.
   */
  Page<RolProyecto> findAllTodos(String query, Pageable pageable);

  /**
   * Activa el {@link RolProyecto}.
   *
   * @param id Id del {@link RolProyecto}.
   * @return la entidad {@link RolProyecto} persistida.
   */
  RolProyecto enable(Long id);

  /**
   * Desactiva el {@link RolProyecto}
   *
   * @param id Id del {@link RolProyecto}.
   * @return la entidad {@link RolProyecto} persistida.
   */
  RolProyecto disable(Long id);
}
