package org.crue.hercules.sgi.csp.repository.custom;

import java.util.Optional;
import java.util.UUID;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudExterna;
import org.springframework.stereotype.Component;

/**
 * Custom repository para {@link SolicitudExterna}.
 */
@Component
public interface CustomSolicitudExternaRepository {

  /**
   * Devuelve el id de la {@link Solicitud} asociada a la
   * {@link SolicitudExterna}.
   *
   * @param uuid id {@link SolicitudExterna}.
   * @return id de la {@link Solicitud} asociada
   */
  Optional<Long> findSolicitudId(UUID uuid);

  /**
   * Devuelve el id de la {@link SolicitudExterna} de la
   * {@link Solicitud} con uuid y numeroDocumentoSolicitante
   * indicados.
   *
   * @param uuid                       codigo interno de la {@link Solicitud}.
   * @param numeroDocumentoSolicitante Numero de documento del solicitante
   *                                   externo.
   * @return id de la {@link SolicitudExterna}
   */
  Optional<UUID> findPublicId(UUID uuid, String numeroDocumentoSolicitante);

}
