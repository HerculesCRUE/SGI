package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoHito}.
 */

public interface ProyectoHitoService {

  /**
   * Guarda la entidad {@link ProyectoHito}.
   * 
   * @param ProyectoHito la entidad {@link ProyectoHito} a guardar.
   * @return ProyectoHito la entidad {@link ProyectoHito} persistida.
   */
  ProyectoHito create(ProyectoHito ProyectoHito);

  /**
   * Actualiza la entidad {@link ProyectoHito}.
   * 
   * @param ProyectoHitoActualizar la entidad {@link ProyectoHito} a guardar.
   * @return ProyectoHito la entidad {@link ProyectoHito} persistida.
   */
  ProyectoHito update(ProyectoHito ProyectoHitoActualizar);

  /**
   * Elimina la {@link ProyectoHito}.
   *
   * @param id Id del {@link ProyectoHito}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ProyectoHito} por su id.
   *
   * @param id el id de la entidad {@link ProyectoHito}.
   * @return la entidad {@link ProyectoHito}.
   */
  ProyectoHito findById(Long id);

  /**
   * Obtiene las {@link ProyectoHito} para una {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoHito} de la {@link Proyecto}
   *         paginadas.
   */
  Page<ProyectoHito> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoHito}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return si existe o no el proyecto
   */
  boolean existsByProyecto(Long proyectoId);

}
