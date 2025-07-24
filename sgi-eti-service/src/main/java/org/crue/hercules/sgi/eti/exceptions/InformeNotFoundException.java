package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * InformeNotFoundException
 */
public class InformeNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_INFORME = "org.crue.hercules.sgi.eti.model.Informe.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public InformeNotFoundException(Long informeId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_INFORME), informeId }));
  }

}