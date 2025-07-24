package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * LanguageNotFoundException
 */
public class LanguageNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_LANGUAGE = "org.crue.hercules.sgi.eti.model.Language.message";
  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public LanguageNotFoundException(String code) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_LANGUAGE), code }));
  }
}