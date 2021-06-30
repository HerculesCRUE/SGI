package org.crue.hercules.sgi.csp.exceptions;

public class TipoEnlaceNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoEnlaceNotFoundException(Long tipoEnlaceId) {
    super("TipoEnlace " + tipoEnlaceId + " does not exist.");
  }
}
