package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConflictoInteresNotFoundException
 */
public class ConflictoInteresNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_CONFLICTO_INTERES = "org.crue.hercules.sgi.eti.model.ConflictoInteres.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConflictoInteresNotFoundException(Long conflictoInteresId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONFLICTO_INTERES), conflictoInteresId }));
  }

}