package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ProyectoFacturacionNotFoundException extends CspNotFoundException {
  /** 
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoFacturacionNotFoundException(Long proyectoFacturacionId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ProyectoFacturacion.class))
        .parameter("id", proyectoFacturacionId).build());

  }

}
