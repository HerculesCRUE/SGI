package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoEvaluacionNotFoundException
 */
public class TipoEvaluacionNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_EVALUACION = "org.crue.hercules.sgi.eti.model.TipoEvaluacion.message";
  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public TipoEvaluacionNotFoundException(Long tipoEvaluacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_EVALUACION), tipoEvaluacionId }));
  }
}