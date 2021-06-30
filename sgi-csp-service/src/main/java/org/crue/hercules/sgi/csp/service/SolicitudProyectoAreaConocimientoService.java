package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoAreaConocimiento}.
 */

public interface SolicitudProyectoAreaConocimientoService {

  /**
   * Guarda la entidad {@link SolicitudProyectoAreaConocimiento}.
   * 
   * @param solicitudProyectoAreaConocimiento la entidad
   *                                          {@link SolicitudProyectoAreaConocimiento}a
   *                                          guardar.
   * @return SolicitudProyectoAreaConocimiento la entidad
   *         {@link SolicitudProyectoAreaConocimiento} persistida.
   */
  SolicitudProyectoAreaConocimiento create(SolicitudProyectoAreaConocimiento solicitudProyectoAreaConocimiento);

  /**
   * Elimina el {@link SolicitudProyectoAreaConocimiento}.
   *
   * @param id Id del {@link SolicitudProyectoAreaConocimiento} a eliminar.
   */
  void delete(Long id);

  /**
   * Obtener todas las entidades {@link SolicitudProyectoAreaConocimiento}
   * paginadas y/o filtradas.
   * 
   * @param id     id de la SolicitudProyecto
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoAreaConocimiento}
   */

  Page<SolicitudProyectoAreaConocimiento> findAllBySolicitudProyectoId(Long id, String query, Pageable paging);

}
