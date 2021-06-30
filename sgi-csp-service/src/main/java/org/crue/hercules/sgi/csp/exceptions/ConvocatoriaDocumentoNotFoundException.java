package org.crue.hercules.sgi.csp.exceptions;

/**
 * ConvocatoriaDocumentoNotFoundException
 */
public class ConvocatoriaDocumentoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaDocumentoNotFoundException(Long convocatoriaDocumentoId) {
    super("ConvocatoriaDocumento " + convocatoriaDocumentoId + " does not exist.");
  }

}
