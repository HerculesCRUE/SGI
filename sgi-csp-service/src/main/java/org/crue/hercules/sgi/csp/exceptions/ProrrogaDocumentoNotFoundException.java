package org.crue.hercules.sgi.csp.exceptions;

public class ProrrogaDocumentoNotFoundException extends CspNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ProrrogaDocumentoNotFoundException(Long ProrrogaDocumentoId) {
    super("ProrrogaDocumento " + ProrrogaDocumentoId + " does not exist.");
  }

}
