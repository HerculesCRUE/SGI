package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudModalidadNotFoundException
 */
public class SolicitudModalidadNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudModalidadNotFoundException(Long solicitudModalidadId) {
    super("SolicitudModalidad " + solicitudModalidadId + " does not exist.");
  }

}
