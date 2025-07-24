package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * EvaluadorNotFoundException
 */
public class EvaluadorNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_EVALUADOR = "org.crue.hercules.sgi.eti.model.Evaluador.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EvaluadorNotFoundException(Long evaluadorId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_EVALUADOR), evaluadorId }));
  }

}