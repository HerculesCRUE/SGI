package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * RequisitoEquipoNotFoundException
 */
public class RequisitoEquipoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_REQUISITO_EQUIPO = "org.crue.hercules.sgi.csp.model.RequisitoEquipo.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RequisitoEquipoNotFoundException(Long requisitoEquipoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_REQUISITO_EQUIPO), requisitoEquipoId }));
  }

}
