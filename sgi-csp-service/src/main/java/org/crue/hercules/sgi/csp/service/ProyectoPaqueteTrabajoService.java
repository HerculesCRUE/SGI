package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPaqueteTrabajo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoPaqueteTrabajo}.
 */

public interface ProyectoPaqueteTrabajoService {

  /**
   * Guarda la entidad {@link ProyectoPaqueteTrabajo}.
   * 
   * @param ProyectoPaqueteTrabajo la entidad {@link ProyectoPaqueteTrabajo} a
   *                               guardar.
   * @return ProyectoPaqueteTrabajo la entidad {@link ProyectoPaqueteTrabajo}
   *         persistida.
   */
  ProyectoPaqueteTrabajo create(ProyectoPaqueteTrabajo ProyectoPaqueteTrabajo);

  /**
   * Actualiza la entidad {@link ProyectoPaqueteTrabajo}.
   * 
   * @param ProyectoPaqueteTrabajoActualizar la entidad
   *                                         {@link ProyectoPaqueteTrabajo} a
   *                                         guardar.
   * @return ProyectoPaqueteTrabajo la entidad {@link ProyectoPaqueteTrabajo}
   *         persistida.
   */
  ProyectoPaqueteTrabajo update(ProyectoPaqueteTrabajo ProyectoPaqueteTrabajoActualizar);

  /**
   * Elimina la {@link ProyectoPaqueteTrabajo}.
   *
   * @param id Id del {@link ProyectoPaqueteTrabajo}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ProyectoPaqueteTrabajo} por su id.
   *
   * @param id el id de la entidad {@link ProyectoPaqueteTrabajo}.
   * @return la entidad {@link ProyectoPaqueteTrabajo}.
   */
  ProyectoPaqueteTrabajo findById(Long id);

  /**
   * Obtiene las {@link ProyectoPaqueteTrabajo} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoPaqueteTrabajo} de la
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoPaqueteTrabajo> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

}
