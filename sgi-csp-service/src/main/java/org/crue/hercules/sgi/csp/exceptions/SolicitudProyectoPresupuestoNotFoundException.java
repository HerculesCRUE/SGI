package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoNotFoundException
 */
public class SolicitudProyectoPresupuestoNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_SOLICITUD_PROYECTO_PRESUPUESTO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoPresupuestoNotFoundException(Long solicitudProyectoPresupuestoId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_PRESUPUESTO),
            solicitudProyectoPresupuestoId }));
  }

}
