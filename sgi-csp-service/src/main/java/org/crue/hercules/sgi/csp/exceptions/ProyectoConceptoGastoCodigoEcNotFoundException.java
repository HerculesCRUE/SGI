package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoConceptoGastoCodigoEcNotFoundException
 */
public class ProyectoConceptoGastoCodigoEcNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_GASTO_CODIGO_EC = "org.crue.hercules.sgi.csp.model.ProyectoGastoCodigoEc.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoConceptoGastoCodigoEcNotFoundException(Long proyectoGastoCodigoEcId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_GASTO_CODIGO_EC),
            proyectoGastoCodigoEcId }));
  }

}
