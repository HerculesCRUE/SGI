package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.AreaTematica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link AreaTematica}.
 */
public interface AreaTematicaService {

  /**
   * Guardar un nuevo {@link AreaTematica}.
   *
   * @param areaTematica la entidad {@link AreaTematica} a guardar.
   * @return la entidad {@link AreaTematica} persistida.
   */
  AreaTematica create(AreaTematica areaTematica);

  /**
   * Actualizar {@link AreaTematica}.
   *
   * @param areaTematicaActualizar la entidad {@link AreaTematica} a actualizar.
   * @return la entidad {@link AreaTematica} persistida.
   */
  AreaTematica update(AreaTematica areaTematicaActualizar);

  /**
   * Reactiva el {@link AreaTematica}.
   *
   * @param id Id del {@link AreaTematica}.
   * @return la entidad {@link AreaTematica} persistida.
   */
  AreaTematica enable(Long id);

  /**
   * Desactiva el {@link AreaTematica}
   *
   * @param id Id del {@link AreaTematica}.
   * @return la entidad {@link AreaTematica} persistida.
   */
  AreaTematica disable(Long id);

  /**
   * Obtiene {@link AreaTematica} por su id.
   *
   * @param id el id de la entidad {@link AreaTematica}.
   * @return la entidad {@link AreaTematica}.
   */
  AreaTematica findById(Long id);

  /**
   * Obtiene los {@link AreaTematica} activos.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  Page<AreaTematica> findAll(String query, Pageable pageable);

  /**
   * Obtiene los grupos activos (los {@link AreaTematica} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  Page<AreaTematica> findAllGrupo(String query, Pageable pageable);

  /**
   * Obtiene los grupos (los {@link AreaTematica} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  Page<AreaTematica> findAllTodosGrupo(String query, Pageable pageable);

  /**
   * Obtiene los {@link AreaTematica} hijos directos del {@link AreaTematica} con
   * el id indicado.
   *
   * @param areaTematicaId el id de la entidad {@link AreaTematica}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link AreaTematica} paginadas.
   */
  Page<AreaTematica> findAllHijosAreaTematica(Long areaTematicaId, String query, Pageable pageable);

}
