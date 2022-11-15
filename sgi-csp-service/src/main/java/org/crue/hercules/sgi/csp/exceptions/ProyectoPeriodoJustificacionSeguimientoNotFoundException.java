package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoPeriodoJustificacionSeguimientoNotFoundException
 */
public class ProyectoPeriodoJustificacionSeguimientoNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public ProyectoPeriodoJustificacionSeguimientoNotFoundException(Long proyectoPeriodoJustificacionSeguimientoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity",
            ApplicationContextSupport.getMessage(ProyectoPeriodoJustificacionSeguimiento.class))
        .parameter("id", proyectoPeriodoJustificacionSeguimientoId).build());
  }
}