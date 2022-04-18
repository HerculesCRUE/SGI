package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacionLog;

/**
 * ConvocatoriaBaremacionLogNotFoundException
 */
public class ConvocatoriaBaremacionLogNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public ConvocatoriaBaremacionLogNotFoundException(Long id) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ConvocatoriaBaremacionLog.class))
        .parameter("id", id).build());
  }
}
