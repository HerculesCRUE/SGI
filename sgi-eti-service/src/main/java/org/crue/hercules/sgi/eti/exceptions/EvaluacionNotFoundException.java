package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * EvaluacionNotFoundException
 */
public class EvaluacionNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_EVALUACION = "org.crue.hercules.sgi.eti.model.Evaluacion.message";

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EvaluacionNotFoundException(Long evaluacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_EVALUACION), evaluacionId }));
  }

}