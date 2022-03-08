package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.enums.TipoFuenteImpacto;

/**
 * TipoFuenteImpactoNotFoundException
 */
public class TipoFuenteImpactoNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public TipoFuenteImpactoNotFoundException(String tipoFuenteImpacto) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(TipoFuenteImpacto.class))
        .parameter("id", tipoFuenteImpacto).build());
  }
}
