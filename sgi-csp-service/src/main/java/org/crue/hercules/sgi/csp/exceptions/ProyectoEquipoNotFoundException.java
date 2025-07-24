package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoEquipoNotFoundException
 */
public class ProyectoEquipoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_EQUIPO = "org.crue.hercules.sgi.csp.model.ProyectoEquipo.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoEquipoNotFoundException(Long proyectoEquipoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_EQUIPO), proyectoEquipoId }));
  }

}
