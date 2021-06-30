package org.crue.hercules.sgi.csp.exceptions;

/**
 * ConvocatoriaConceptoGastoCodigoEcNotFoundException
 */
public class ConvocatoriaConceptoGastoCodigoEcNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaConceptoGastoCodigoEcNotFoundException(Long convocatoriaGastoCodigoEcId) {
    super("ConvocatoriaConceptoGastoCodigoEc " + convocatoriaGastoCodigoEcId + " does not exist.");
  }

}
