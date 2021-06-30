package org.crue.hercules.sgi.csp.exceptions;

/**
 * TipoDocumentoNotFoundException
 */
public class TipoDocumentoNotFoundException extends CspNotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public TipoDocumentoNotFoundException(Long tipoDocumentoId) {
    super("TipoDocumento " + tipoDocumentoId + " does not exist.");
  }

}