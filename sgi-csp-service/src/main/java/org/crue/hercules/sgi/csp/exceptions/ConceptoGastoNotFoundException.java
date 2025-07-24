package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConceptoGastoNotFoundException
 */
public class ConceptoGastoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONCEPTO_GASTO = "org.crue.hercules.sgi.csp.model.ConceptoGasto.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConceptoGastoNotFoundException(Long conceptoGastoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONCEPTO_GASTO), conceptoGastoId }));
  }

}
