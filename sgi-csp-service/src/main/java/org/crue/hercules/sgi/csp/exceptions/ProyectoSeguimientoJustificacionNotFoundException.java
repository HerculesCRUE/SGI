package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoSeguimientoJustificacionNotFoundException
 */
public class ProyectoSeguimientoJustificacionNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public ProyectoSeguimientoJustificacionNotFoundException(Long proyectoSeguimientoJustificacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ProyectoSeguimientoJustificacion.class))
        .parameter("id", proyectoSeguimientoJustificacionId).build());
  }
}