package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoConceptoGastoNotFoundException
 */
public class ProyectoConceptoGastoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_CONCEPTO_GASTO = "org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoConceptoGastoNotFoundException(Long proyectoConceptoGastoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_CONCEPTO_GASTO),
            proyectoConceptoGastoId }));
  }

}
