package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.CertificadoAutorizacion;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class CertificadoAutorizacionNotFoundException extends CspNotFoundException {

  /**
   * CertificadoAutorizacionNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public CertificadoAutorizacionNotFoundException(Long certificadoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(
            CertificadoAutorizacion.class))
        .parameter("id", certificadoId).build());
  }
}
