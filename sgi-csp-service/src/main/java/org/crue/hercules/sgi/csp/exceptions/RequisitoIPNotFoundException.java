package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * RequisitoIPNotFoundException
 */
public class RequisitoIPNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_REQUISITO_IP = "org.crue.hercules.sgi.csp.model.RequisitoIP.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RequisitoIPNotFoundException(Long requisitoIPId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_REQUISITO_IP), requisitoIPId }));
  }

}
