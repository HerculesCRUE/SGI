package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConvocatoriaReunionNotFoundException
 */
public class ConvocatoriaReunionNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_REUNION = "org.crue.hercules.sgi.eti.model.ConvocatoriaReunion.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaReunionNotFoundException(Long convocatoriaReunionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_REUNION), convocatoriaReunionId }));
  }

}