package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudHitoNotFoundException
 */
public class SolicitudHitoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudHitoNotFoundException(Long solicitudHitoId) {
    super("Solicitud hito " + solicitudHitoId + " does not exist.");
  }
}
