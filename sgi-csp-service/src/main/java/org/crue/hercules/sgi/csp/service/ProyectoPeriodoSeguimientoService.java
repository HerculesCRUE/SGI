package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoPeriodoSeguimiento}.
 */

public interface ProyectoPeriodoSeguimientoService {

  /**
   * Guarda la entidad {@link ProyectoPeriodoSeguimiento}.
   * 
   * @param proyectoPeriodoSeguimiento la entidad
   *                                   {@link ProyectoPeriodoSeguimiento} a
   *                                   guardar.
   * @return ProyectoPeriodoSeguimiento la entidad
   *         {@link ProyectoPeriodoSeguimiento} persistida.
   */
  ProyectoPeriodoSeguimiento create(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento);

  /**
   * Actualiza la entidad {@link ProyectoPeriodoSeguimiento}.
   * 
   * @param proyectoPeriodoSeguimientoActualizar la entidad
   *                                             {@link ProyectoPeriodoSeguimiento}
   *                                             a guardar.
   * @return ProyectoPeriodoSeguimiento la entidad
   *         {@link ProyectoPeriodoSeguimiento} persistida.
   */
  ProyectoPeriodoSeguimiento update(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimientoActualizar);

  /**
   * Elimina la {@link ProyectoPeriodoSeguimiento}.
   *
   * @param id Id del {@link ProyectoPeriodoSeguimiento}.
   */
  void delete(Long id);

  /**
   * Comprueba la existencia del {@link ProyectoPeriodoSeguimiento} por id.
   *
   * @param id el id de la entidad {@link ProyectoPeriodoSeguimiento}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene {@link ProyectoPeriodoSeguimiento} por su id.
   *
   * @param id el id de la entidad {@link ProyectoPeriodoSeguimiento}.
   * @return la entidad {@link ProyectoPeriodoSeguimiento}.
   */
  ProyectoPeriodoSeguimiento findById(Long id);

  /**
   * Obtiene las entidades {@link ProyectoPeriodoSeguimiento} para un
   * {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoPeriodoSeguimiento} del
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoPeriodoSeguimiento> findAllByProyecto(Long proyectoId, String query, Pageable pageable);

}
