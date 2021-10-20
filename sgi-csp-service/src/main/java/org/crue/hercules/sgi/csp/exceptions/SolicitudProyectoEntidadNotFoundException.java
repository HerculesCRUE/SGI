package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * SolicitudProyectoEntidadNotFoundException
 */
public class SolicitudProyectoEntidadNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoEntidadNotFoundException(Long fuenteFinanciacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProyectoEntidad.class))
        .parameter("id", fuenteFinanciacionId).build());
  }
}
