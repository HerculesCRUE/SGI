package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoConceptoGastoCodigoEcNotFoundException
 */
public class ProyectoConceptoGastoCodigoEcNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoConceptoGastoCodigoEcNotFoundException(Long proyectoGastoCodigoEcId) {
    super("ProyectoConceptoGastoCodigoEc " + proyectoGastoCodigoEcId + " does not exist.");
  }

}
