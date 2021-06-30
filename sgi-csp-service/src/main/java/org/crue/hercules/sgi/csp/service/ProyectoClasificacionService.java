package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoClasificacion}.
 */
public interface ProyectoClasificacionService {

  /**
   * Guardar un nuevo {@link ProyectoClasificacion}.
   *
   * @param proyectoClasificacion la entidad {@link ProyectoClasificacion} a
   *                              guardar.
   * @return la entidad {@link ProyectoClasificacion} persistida.
   */
  ProyectoClasificacion create(ProyectoClasificacion proyectoClasificacion);

  /**
   * Elimina el {@link ProyectoClasificacion}.
   *
   * @param id Id del {@link ProyectoClasificacion}.
   */
  void delete(Long id);

  /**
   * Obtiene los {@link ProyectoClasificacion} para un {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoClasificacion} de la
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoClasificacion> findAllByProyecto(Long proyectoId, String query, Pageable pageable);
}