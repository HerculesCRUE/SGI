package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConvocatoriaConceptoGastoNotFoundException
 */
public class ConvocatoriaConceptoGastoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO = "org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaConceptoGastoNotFoundException(Long convocatoriaConceptoGastoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO),
            convocatoriaConceptoGastoId }));
  }

}
