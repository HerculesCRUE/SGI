package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoAmortizacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ProyectoPeriodoAmortizacionNotFoundException
 */
public class ProyectoPeriodoAmortizacionNotFoundException extends CspNotFoundException {

  /** 
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPeriodoAmortizacionNotFoundException(Long proyectoPeriodoAmortizacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ProyectoPeriodoAmortizacion.class))
        .parameter("id", proyectoPeriodoAmortizacionId).build());
  }
}
