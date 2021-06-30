package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoFase}.
 */

public interface ProyectoFaseService {

  /**
   * Guarda la entidad {@link ProyectoFase}.
   * 
   * @param ProyectoFase la entidad {@link ProyectoFase} a guardar.
   * @return ProyectoFase la entidad {@link ProyectoFase} persistida.
   */
  ProyectoFase create(ProyectoFase ProyectoFase);

  /**
   * Actualiza la entidad {@link ProyectoFase}.
   * 
   * @param ProyectoFaseActualizar la entidad {@link ProyectoFase} a guardar.
   * @return ProyectoFase la entidad {@link ProyectoFase} persistida.
   */
  ProyectoFase update(ProyectoFase ProyectoFaseActualizar);

  /**
   * Elimina la {@link ProyectoFase}.
   *
   * @param id Id del {@link ProyectoFase}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ProyectoFase} por su id.
   *
   * @param id el id de la entidad {@link ProyectoFase}.
   * @return la entidad {@link ProyectoFase}.
   */
  ProyectoFase findById(Long id);

  /**
   * Obtiene las {@link ProyectoFase} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoFase} de la {@link Proyecto}
   *         paginadas.
   */
  Page<ProyectoFase> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoFase}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return si existe o no el proyecto
   */
  boolean existsByProyecto(Long proyectoId);

}
