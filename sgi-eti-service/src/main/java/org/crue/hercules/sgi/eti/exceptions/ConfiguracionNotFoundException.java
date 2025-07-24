package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ConfiguracionNotFoundException
 */
public class ConfiguracionNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_CONFIGURACION = "org.crue.hercules.sgi.eti.model.Configuracion.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConfiguracionNotFoundException(Long idConfiguracion) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_CONFIGURACION), idConfiguracion != null ? idConfiguracion.toString() : "" }));
  }

}