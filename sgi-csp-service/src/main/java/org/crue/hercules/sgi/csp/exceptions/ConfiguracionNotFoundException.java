package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConfiguracionNotFoundException
 */
public class ConfiguracionNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONFIGURACION = "org.crue.hercules.sgi.csp.model.Configuracion.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConfiguracionNotFoundException(Long configuracionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONFIGURACION), configuracionId }));
  }

}
