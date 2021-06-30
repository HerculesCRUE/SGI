package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Programa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Programa}.
 */
public interface ProgramaService {

  /**
   * Guardar un nuevo {@link Programa}.
   *
   * @param programa la entidad {@link Programa} a guardar.
   * @return la entidad {@link Programa} persistida.
   */
  Programa create(Programa programa);

  /**
   * Actualizar {@link Programa}.
   *
   * @param programaActualizar la entidad {@link Programa} a actualizar.
   * @return la entidad {@link Programa} persistida.
   */
  Programa update(Programa programaActualizar);

  /**
   * Reactiva el {@link Programa}.
   *
   * @param id Id del {@link Programa}.
   * @return la entidad {@link Programa} persistida.
   */
  Programa enable(Long id);

  /**
   * Desactiva el {@link Programa}.
   *
   * @param id Id del {@link Programa}.
   * @return la entidad {@link Programa} persistida.
   */
  Programa disable(Long id);

  /**
   * Obtiene {@link Programa} por su id.
   *
   * @param id el id de la entidad {@link Programa}.
   * @return la entidad {@link Programa}.
   */
  Programa findById(Long id);

  /**
   * Obtiene los {@link Programa} activos.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  Page<Programa> findAll(String query, Pageable pageable);

  /**
   * Obtiene los planes activos (los {@link Programa} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  Page<Programa> findAllPlan(String query, Pageable pageable);

  /**
   * Obtiene los planes (los {@link Programa} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  Page<Programa> findAllTodosPlan(String query, Pageable pageable);

  /**
   * Obtiene los {@link Programa} hijos directos del {@link Programa} con el id
   * indicado.
   *
   * @param programaId el id de la entidad {@link Programa}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  Page<Programa> findAllHijosPrograma(Long programaId, String query, Pageable pageable);

}
