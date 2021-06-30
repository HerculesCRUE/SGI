package org.crue.hercules.sgi.csp.exceptions;

/**
 * ModeloTipoEnlaceNotFoundException
 */
public class ModeloTipoEnlaceNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoEnlaceNotFoundException(Long modeloTipoEnlaceId) {
    super("ModeloTipoEnlace " + modeloTipoEnlaceId + " does not exist.");
  }
}
