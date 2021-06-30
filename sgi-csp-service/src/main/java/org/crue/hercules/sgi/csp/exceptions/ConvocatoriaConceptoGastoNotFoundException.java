package org.crue.hercules.sgi.csp.exceptions;

/**
 * ConvocatoriaConceptoGastoNotFoundException
 */
public class ConvocatoriaConceptoGastoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaConceptoGastoNotFoundException(Long convocatoriaConceptoGastoId) {
    super("ConvocatoriaConceptoGasto " + convocatoriaConceptoGastoId + " does not exist.");
  }

}
