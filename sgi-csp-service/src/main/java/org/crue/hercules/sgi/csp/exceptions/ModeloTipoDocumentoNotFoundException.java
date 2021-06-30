package org.crue.hercules.sgi.csp.exceptions;

/**
 * ModeloTipoDocumentoNotFoundException
 */
public class ModeloTipoDocumentoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoDocumentoNotFoundException(Long modeloTipoDocumentoId) {
    super("ModeloTipoDocumento " + modeloTipoDocumentoId + " does not exist.");
  }
}
