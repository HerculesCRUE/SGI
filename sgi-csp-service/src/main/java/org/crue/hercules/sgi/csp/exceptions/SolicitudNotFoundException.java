package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudNotFoundException
 */
public class SolicitudNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudNotFoundException(Long solicitudId) {
    super("Solicitud " + solicitudId + " does not exist.");
  }

}
