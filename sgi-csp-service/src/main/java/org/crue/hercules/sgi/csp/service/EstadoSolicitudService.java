package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link EstadoSolicitud}.
 */
public interface EstadoSolicitudService {

  /**
   * Guarda la entidad {@link EstadoSolicitud}.
   * 
   * @param estadoSolicitud la entidad {@link EstadoSolicitud} a guardar.
   * @return solicitudModalidad la entidad {@link EstadoSolicitud} persistida.
   */
  EstadoSolicitud create(EstadoSolicitud estadoSolicitud);

  /**
   * Obtiene las {@link EstadoSolicitud} para una {@link Solicitud}.
   *
   * @param idSolicitud el id de la {@link Solicitud}.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link EstadoSolicitud} de la {@link Solicitud}
   *         paginadas.
   */
  Page<EstadoSolicitud> findAllBySolicitud(Long idSolicitud, Pageable pageable);

}
