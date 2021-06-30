package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoPeriodoSeguimientoDocumento}.
 */

public interface ProyectoPeriodoSeguimientoDocumentoService {

  /**
   * Guarda la entidad {@link ProyectoPeriodoSeguimientoDocumento}.
   * 
   * @param proyectoPeriodoSeguimientoDocumento la entidad
   *                                            {@link ProyectoPeriodoSeguimientoDocumento}
   *                                            a guardar.
   * @return ProyectoPeriodoSeguimientoDocumento la entidad
   *         {@link ProyectoPeriodoSeguimientoDocumento} persistida.
   */
  ProyectoPeriodoSeguimientoDocumento create(ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento);

  /**
   * Actualiza los datos del {@link ProyectoPeriodoSeguimientoDocumento}.
   * 
   * @param proyectoPeriodoSeguimientoDocumento {@link ProyectoPeriodoSeguimientoDocumento}
   *                                            con los datos actualizados.
   * @return ProyectoPeriodoSeguimientoDocumento
   *         {@link ProyectoPeriodoSeguimientoDocumento} actualizado.
   */
  ProyectoPeriodoSeguimientoDocumento update(
      final ProyectoPeriodoSeguimientoDocumento proyectoPeriodoSeguimientoDocumento);

  /**
   * Obtiene una entidad {@link ProyectoPeriodoSeguimientoDocumento} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link ProyectoPeriodoSeguimientoDocumento}.
   * @return ProyectoPeriodoSeguimientoDocumento la entidad
   *         {@link ProyectoPeriodoSeguimientoDocumento}.
   */
  ProyectoPeriodoSeguimientoDocumento findById(final Long id);

  /**
   * Elimina el {@link ProyectoPeriodoSeguimientoDocumento}.
   *
   * @param id Id del {@link ProyectoPeriodoSeguimientoDocumento}.
   */
  void delete(Long id);

  /**
   * Obtiene las {@link ProyectoPeriodoSeguimientoDocumento} para una
   * {@link ProyectoPeriodoSeguimiento}.
   *
   * @param proyectoPeriodoSeguimientoId el id de la
   *                                     {@link ProyectoPeriodoSeguimiento}.
   * @param query                        la información del filtro.
   * @param pageable                     la información de la paginación.
   * @return la lista de entidades {@link ProyectoPeriodoSeguimientoDocumento} de
   *         la {@link ProyectoPeriodoSeguimiento} paginadas.
   */
  Page<ProyectoPeriodoSeguimientoDocumento> findAllByProyectoPeriodoSeguimiento(Long proyectoPeriodoSeguimientoId,
      String query, Pageable pageable);

  /**
   * Comprueba la existencia del {@link ProyectoPeriodoSeguimientoDocumento} por
   * id de {@link ProyectoPeriodoSeguimiento}
   *
   * @param id el id de la entidad {@link ProyectoPeriodoSeguimiento}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyectoPeriodoSeguimiento(Long id);

  /**
   * Obtiene las {@link ProyectoPeriodoSeguimientoDocumento} para una
   * {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @return la lista de entidades {@link ProyectoPeriodoSeguimientoDocumento} de
   *         la {@link Proyecto}.
   */
  List<ProyectoPeriodoSeguimientoDocumento> findAllByProyecto(Long proyectoId);

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProyectoPeriodoSeguimientoDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyecto(Long proyectoId);

}
