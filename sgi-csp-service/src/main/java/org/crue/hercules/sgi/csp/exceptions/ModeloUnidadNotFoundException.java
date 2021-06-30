package org.crue.hercules.sgi.csp.exceptions;

/**
 * ModeloUnidadNotFoundException
 */
public class ModeloUnidadNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloUnidadNotFoundException(Long modeloUnidadId) {
    super("ModeloUnidad " + modeloUnidadId + " does not exist.");
  }
}
