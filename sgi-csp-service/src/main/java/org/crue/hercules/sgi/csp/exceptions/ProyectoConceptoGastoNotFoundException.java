package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoConceptoGastoNotFoundException
 */
public class ProyectoConceptoGastoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoConceptoGastoNotFoundException(Long proyectoConceptoGastoId) {
    super("ProyectoConceptoGasto " + proyectoConceptoGastoId + " does not exist.");
  }

}
