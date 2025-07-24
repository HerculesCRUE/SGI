package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * AsistentesNotFoundException
 */
public class AsistentesNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_ASISTENTES = "org.crue.hercules.sgi.eti.model.Asistentes.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public AsistentesNotFoundException(Long asistentesId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_ASISTENTES), asistentesId }));
  }

}