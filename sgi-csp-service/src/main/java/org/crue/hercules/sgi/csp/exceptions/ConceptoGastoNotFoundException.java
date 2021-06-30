package org.crue.hercules.sgi.csp.exceptions;

/**
 * ConceptoGastoNotFoundException
 */
public class ConceptoGastoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConceptoGastoNotFoundException(Long conceptoGastoId) {
    super("ConceptoGasto " + conceptoGastoId + " does not exist.");
  }

}
