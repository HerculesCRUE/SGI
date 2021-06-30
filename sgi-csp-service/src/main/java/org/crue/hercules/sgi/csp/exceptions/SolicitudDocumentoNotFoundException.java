package org.crue.hercules.sgi.csp.exceptions;

public class SolicitudDocumentoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudDocumentoNotFoundException(Long solicitudDocumentoId) {
    super("SolicitudDocumento " + solicitudDocumentoId + " does not exist.");
  }
}
