package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * AlegacionRequerimientoNotFoundException
 */
public class AlegacionRequerimientoNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public AlegacionRequerimientoNotFoundException(Long alegacionRequerimientoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(AlegacionRequerimiento.class))
        .parameter("id", alegacionRequerimientoId).build());
  }
}
