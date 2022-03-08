package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica.EpigrafeCVN;

/**
 * EpigrafeCVNNotFoundException
 */
public class EpigrafeCVNNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public EpigrafeCVNNotFoundException(String epigrafeCVN) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(EpigrafeCVN.class))
        .parameter("id", epigrafeCVN).build());
  }
}
