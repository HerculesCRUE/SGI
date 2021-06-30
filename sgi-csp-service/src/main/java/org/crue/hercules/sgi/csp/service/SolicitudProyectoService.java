package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;

/**
 * Service Interface para gestionar {@link SolicitudProyecto}.
 */

public interface SolicitudProyectoService {

  /**
   * Guarda la entidad {@link SolicitudProyecto}.
   * 
   * @param solicitudProyecto la entidad {@link SolicitudProyecto} a guardar.
   * @return SolicitudProyecto la entidad {@link SolicitudProyecto} persistida.
   */
  SolicitudProyecto create(SolicitudProyecto solicitudProyecto);

  /**
   * Actualiza los datos del {@link SolicitudProyecto}.
   * 
   * @param solicitudProyecto {@link SolicitudProyecto} con los datos
   *                          actualizados.
   * @return SolicitudProyecto {@link SolicitudProyecto} actualizado.
   */
  SolicitudProyecto update(final SolicitudProyecto solicitudProyecto);

  /**
   * Obtiene una entidad {@link SolicitudProyecto} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyecto}.
   * @return SolicitudProyecto la entidad {@link SolicitudProyecto}.
   */
  SolicitudProyecto findById(final Long id);

  /**
   * Elimina el {@link SolicitudProyecto}.
   *
   * @param id Id del {@link SolicitudProyecto}.
   */
  void delete(Long id);

  /**
   * Obtiene la {@link SolicitudProyecto} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @return la lista de entidades {@link SolicitudProyecto} de la
   *         {@link Solicitud} paginadas.
   */
  SolicitudProyecto findBySolicitud(Long solicitudId);

  /**
   * Comprueba si existe una solicitud de proyecto
   * 
   * @param id Identificador de la {@link Solicitud}
   * @return Indicador de si existe o no solicitud datos proyecto.
   */
  boolean existsBySolicitudId(Long id);

}