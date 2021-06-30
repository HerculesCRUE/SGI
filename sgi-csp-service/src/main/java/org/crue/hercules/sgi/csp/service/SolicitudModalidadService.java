package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudModalidad}.
 */
public interface SolicitudModalidadService {

  /**
   * Guarda la entidad {@link SolicitudModalidad}.
   * 
   * @param solicitudModalidad la entidad {@link SolicitudModalidad} a guardar.
   * @return solicitudModalidad la entidad {@link SolicitudModalidad} persistida.
   */
  SolicitudModalidad create(SolicitudModalidad solicitudModalidad);

  /**
   * Actualiza los datos del {@link SolicitudModalidad}.
   * 
   * @param solicitudModalidad {@link SolicitudModalidad} con los datos
   *                           actualizados.
   * @return solicitudModalidad {@link SolicitudModalidad} actualizado.
   */
  SolicitudModalidad update(final SolicitudModalidad solicitudModalidad);

  /**
   * Elimina el {@link SolicitudModalidad}.
   *
   * @param id Id del {@link SolicitudModalidad}.
   */
  void delete(Long id);

  /**
   * Obtiene una entidad {@link SolicitudModalidad} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudModalidad}.
   * @return solicitudModalidad la entidad {@link SolicitudModalidad}.
   */
  SolicitudModalidad findById(final Long id);

  /**
   * Obtiene las {@link SolicitudModalidad} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudModalidad} de la
   *         {@link Solicitud} paginadas.
   */
  Page<SolicitudModalidad> findAllBySolicitud(Long solicitudId, String query, Pageable pageable);

}
