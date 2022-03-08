package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;

/**
 * ProduccionCientificaNotFoundException
 */
public class ProduccionCientificaNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public ProduccionCientificaNotFoundException(String produccionCientificaIdOrRef) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ProduccionCientifica.class))
        .parameter("id", produccionCientificaIdOrRef).build());
  }
}
