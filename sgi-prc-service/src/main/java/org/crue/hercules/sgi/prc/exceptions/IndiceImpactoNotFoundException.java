package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;

/**
 * IndiceImpactoNotFoundException
 */
public class IndiceImpactoNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public IndiceImpactoNotFoundException(Long idIndiceImpacto) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(IndiceImpacto.class))
        .parameter("id", idIndiceImpacto).build());
  }
}
