package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * DictamenNotFoundException
 */
public class DictamenNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_DICTAMEN = "org.crue.hercules.sgi.eti.model.Dictamen.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public DictamenNotFoundException(Long dictamenId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_DICTAMEN), dictamenId }));
  }

}