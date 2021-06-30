package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar
 * {@link SolicitudProyectoSocioPeriodoJustificacion}.
 */

public interface SolicitudProyectoSocioPeriodoJustificacionService {

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocioPeriodoJustificacion}.
   * 
   * @param solicitudProyectoSocioId        Id de la
   *                                        {@link SolicitudProyectoSocio}.
   * @param solicitudPeriodoJustificaciones lista con los nuevos
   *                                        {@link SolicitudProyectoSocioPeriodoJustificacion}
   *                                        a guardar.
   * @return Listado de SolicitudProyectoSocioPeriodoJustificacion
   *         {@link SolicitudProyectoSocioPeriodoJustificacion} actualizado.
   */
  List<SolicitudProyectoSocioPeriodoJustificacion> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoSocioPeriodoJustificacion> solicitudPeriodoJustificaciones);

  /**
   * Comprueba la existencia del
   * {@link SolicitudProyectoSocioPeriodoJustificacion} por id.
   *
   * @param id el id de la entidad
   *           {@link SolicitudProyectoSocioPeriodoJustificacion}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocioPeriodoJustificacion} por
   * id.
   * 
   * @param id Identificador de la entidad
   *           {@link SolicitudProyectoSocioPeriodoJustificacion}.
   * @return SolicitudProyectoSocioPeriodoJustificacion la entidad
   *         {@link SolicitudProyectoSocioPeriodoJustificacion}.
   */
  SolicitudProyectoSocioPeriodoJustificacion findById(final Long id);

  /**
   * Elimina el {@link SolicitudProyectoSocioPeriodoJustificacion}.
   *
   * @param id Id del {@link SolicitudProyectoSocioPeriodoJustificacion}.
   */
  void delete(Long id);

  /**
   * Obtiene la {@link SolicitudProyectoSocioPeriodoJustificacion} para una
   * {@link SolicitudProyectoSocio}.
   *
   * @param solicitudProyectoSocioId el id de la {@link SolicitudProyectoSocio}.
   * @param query                    parámentros de búsqueda.
   * @param paging                   parámetros de paginación.
   * @return la lista de entidades
   *         {@link SolicitudProyectoSocioPeriodoJustificacion} de la
   *         {@link SolicitudProyectoSocio} paginadas.
   */
  Page<SolicitudProyectoSocioPeriodoJustificacion> findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId,
      String query, Pageable paging);

}