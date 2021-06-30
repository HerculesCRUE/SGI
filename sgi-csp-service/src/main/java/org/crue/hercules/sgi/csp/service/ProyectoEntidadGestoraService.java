package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadGestora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoEntidadGestora}.
 */

public interface ProyectoEntidadGestoraService {

  /**
   * Guarda la entidad {@link ProyectoEntidadGestora}.
   * 
   * @param proyectoEntidadGestora la entidad {@link ProyectoEntidadGestora} a
   *                               guardar.
   * @return ProyectoEntidadGestora la entidad {@link ProyectoEntidadGestora}
   *         persistida.
   */
  ProyectoEntidadGestora create(ProyectoEntidadGestora proyectoEntidadGestora);

  /**
   * Actualiza la entidad {@link ProyectoEntidadGestora}.
   * 
   * @param proyectoEntidadGestoraActualizar la entidad
   *                                         {@link ProyectoEntidadGestora} a
   *                                         guardar.
   * @return ProyectoEntidadGestora la entidad {@link ProyectoEntidadGestora}
   *         persistida.
   */
  ProyectoEntidadGestora update(ProyectoEntidadGestora proyectoEntidadGestoraActualizar);

  /**
   * Elimina la {@link ProyectoEntidadGestora}.
   *
   * @param id Id del {@link ProyectoEntidadGestora}.
   */
  void delete(Long id);

  /**
   * Obtiene las {@link ProyectoEntidadGestora} para una {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoEntidadGestora} de la
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoEntidadGestora> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

}
