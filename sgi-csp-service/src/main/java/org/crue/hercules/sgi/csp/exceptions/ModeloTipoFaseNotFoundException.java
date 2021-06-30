package org.crue.hercules.sgi.csp.exceptions;

/**
 * ModeloTipoFaseNotFoundException
 */
public class ModeloTipoFaseNotFoundException extends CspNotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public ModeloTipoFaseNotFoundException(Long modeloTipoFaseId) {
    super("ModeloTipoFase " + modeloTipoFaseId + " does not exist.");
  }

}
