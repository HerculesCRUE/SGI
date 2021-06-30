package org.crue.hercules.sgi.csp.exceptions;

public class DocumentoRequeridoSolicitudNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DocumentoRequeridoSolicitudNotFoundException(Long documentoRequeridoSolicitudId) {
    super("DocumentoRequeridoSolicitud with Id " + documentoRequeridoSolicitudId + " does not exist.");
  }
}
