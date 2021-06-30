package org.crue.hercules.sgi.csp.exceptions;

/**
 * EstadoSolicitudNotFoundException
 */
public class EstadoSolicitudNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public EstadoSolicitudNotFoundException(Long estadoSolicitudId) {
    super("EstadoSolicitud " + estadoSolicitudId + " does not exist.");
  }

}
