package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoAreaConocimiento}.
 */

public interface ProyectoAreaConocimientoService {

  /**
   * Guarda la entidad {@link ProyectoAreaConocimiento}.
   * 
   * @param proyectoAreaConocimiento la entidad {@link ProyectoAreaConocimiento} a
   *                                 guardar.
   * @return ProyectoAreaConocimiento la entidad {@link ProyectoAreaConocimiento}
   *         persistida.
   */
  ProyectoAreaConocimiento create(ProyectoAreaConocimiento proyectoAreaConocimiento);

  /**
   * Elimina el {@link ProyectoAreaConocimiento}.
   *
   * @param id Id del {@link ProyectoAreaConocimiento} a eliminar.
   */
  void delete(Long id);

  /**
   * Obtener todas las entidades {@link ProyectoAreaConocimiento} paginadas y/o
   * filtradas.
   * 
   * @param id     id de la Proyecto
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link ProyectoAreaConocimiento}
   */

  Page<ProyectoAreaConocimiento> findAllByProyectoId(Long id, String query, Pageable paging);

}
