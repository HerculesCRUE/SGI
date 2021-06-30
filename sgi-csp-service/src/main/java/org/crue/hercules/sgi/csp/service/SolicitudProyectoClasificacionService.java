package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoClasificacion}.
 */
public interface SolicitudProyectoClasificacionService {

  /**
   * Guardar un nuevo {@link SolicitudProyectoClasificacion}.
   *
   * @param solicitudProyectoClasificacion la entidad
   *                                       {@link SolicitudProyectoClasificacion}
   *                                       a guardar.
   * @return la entidad {@link SolicitudProyectoClasificacion} persistida.
   */
  SolicitudProyectoClasificacion create(SolicitudProyectoClasificacion solicitudProyectoClasificacion);

  /**
   * Elimina el {@link SolicitudProyectoClasificacion}.
   *
   * @param id Id del {@link SolicitudProyectoClasificacion}.
   */
  void delete(Long id);

  /**
   * Obtiene las {@link SolicitudProyectoClasificacion} para una
   * {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoClasificacion} de la
   *         {@link Solicitud} paginadas.
   */
  Page<SolicitudProyectoClasificacion> findAllBySolicitud(Long solicitudId, String query, Pageable pageable);
}