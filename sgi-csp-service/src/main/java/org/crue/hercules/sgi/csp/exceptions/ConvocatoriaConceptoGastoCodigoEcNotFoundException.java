package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConvocatoriaConceptoGastoCodigoEcNotFoundException
 */
public class ConvocatoriaConceptoGastoCodigoEcNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC = "org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaConceptoGastoCodigoEcNotFoundException(Long convocatoriaGastoCodigoEcId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC), convocatoriaGastoCodigoEcId }));
  }

}
