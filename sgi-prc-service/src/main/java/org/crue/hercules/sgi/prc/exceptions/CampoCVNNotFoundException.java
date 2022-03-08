package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica.CodigoCVN;

/**
 * CampoCVNNotFoundException
 */
public class CampoCVNNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public CampoCVNNotFoundException(String campoCVN) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(CodigoCVN.class))
        .parameter("id", campoCVN).build());
  }
}
