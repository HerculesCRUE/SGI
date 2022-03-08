package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.enums.TablaMaestraCVN;

/**
 * TablaMaestraCVNNotFoundException
 */
public class TablaMaestraCVNNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public TablaMaestraCVNNotFoundException(String tablaMaestraCVN) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(TablaMaestraCVN.class))
        .parameter("id", tablaMaestraCVN).build());
  }
}
