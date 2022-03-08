package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;

/**
 * CampoProduccionCientificaNotFoundException
 */
public class CampoProduccionCientificaNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public CampoProduccionCientificaNotFoundException(String campoProduccionCientificaIdOrCodigoCVN) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(CampoProduccionCientifica.class))
        .parameter("id", campoProduccionCientificaIdOrCodigoCVN).build());
  }
}
