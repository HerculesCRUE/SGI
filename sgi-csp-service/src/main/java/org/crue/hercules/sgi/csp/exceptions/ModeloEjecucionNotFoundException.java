package org.crue.hercules.sgi.csp.exceptions;

/**
 * ModeloEjecucionNotFoundException
 */
public class ModeloEjecucionNotFoundException extends CspNotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public ModeloEjecucionNotFoundException(Long modeloEjecucionId) {
    super("ModeloEjecucion " + modeloEjecucionId + " does not exist.");
  }

}