package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.Solicitud;

/**
 * SolicitudNotFoundException
 */
public class SolicitudNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudNotFoundException(Long solicitudId) {
    super(solicitudId, Solicitud.class);
  }

}
