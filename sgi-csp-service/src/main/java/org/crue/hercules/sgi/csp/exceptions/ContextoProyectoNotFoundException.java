package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ContextoProyectoNotFoundException
 */
public class ContextoProyectoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONTEXTO_PROYECTO = "org.crue.hercules.sgi.csp.model.ContextoProyecto.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ContextoProyectoNotFoundException(Long contextoProyectoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONTEXTO_PROYECTO), contextoProyectoId }));
  }

}
