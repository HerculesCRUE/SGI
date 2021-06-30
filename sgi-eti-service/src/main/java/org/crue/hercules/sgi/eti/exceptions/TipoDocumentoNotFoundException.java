package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoDocumentoNotFoundException
 */
public class TipoDocumentoNotFoundException extends EtiNotFoundException {

  /**
   * Serial Version.
   */
  private static final long serialVersionUID = 1L;

  public TipoDocumentoNotFoundException(Long tipoDocumentoId) {
    super("TipoDocumento " + tipoDocumentoId + " does not exist.");
  }

}