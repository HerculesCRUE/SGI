package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoSocioEquipoNotFoundException
 */
public class ProyectoSocioEquipoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_PROYECTO_SOCIO_EQUIPO = "org.crue.hercules.sgi.csp.model.ProyectoSocioEquipo.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioEquipoNotFoundException(Long proyectoSocioEquipoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_PROYECTO_SOCIO_EQUIPO), proyectoSocioEquipoId }));
  }

}
