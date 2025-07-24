package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * PeticionEvaluacionNotFoundException
 */
public class PeticionEvaluacionNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_PETICION_EVALUACION = "org.crue.hercules.sgi.eti.model.PeticionEvaluacion.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public PeticionEvaluacionNotFoundException(Long peticionEvaluacionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PETICION_EVALUACION),
            peticionEvaluacionId }));
  }

}