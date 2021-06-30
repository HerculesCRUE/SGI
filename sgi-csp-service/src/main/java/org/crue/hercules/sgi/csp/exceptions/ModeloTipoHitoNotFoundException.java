package org.crue.hercules.sgi.csp.exceptions;

public class ModeloTipoHitoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoHitoNotFoundException(Long modeloTipoHitoId) {
    super("ModeloTipoHito " + modeloTipoHitoId + " does not exist.");
  }
}
