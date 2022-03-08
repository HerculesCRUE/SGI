package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;

/**
 * EstadoProduccionCientificaNotFoundException
 */
public class EstadoProduccionCientificaNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public EstadoProduccionCientificaNotFoundException(String estadoProduccionCientificaIdOrCodigoCVN) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(EstadoProduccionCientifica.class))
        .parameter("id", estadoProduccionCientificaIdOrCodigoCVN).build());
  }
}
