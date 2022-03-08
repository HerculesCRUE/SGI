package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;

/**
 * ConvocatoriaBaremacionNotFoundException
 */
public class ConvocatoriaBaremacionNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public ConvocatoriaBaremacionNotFoundException(Long id) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ConvocatoriaBaremacion.class))
        .parameter("id", id).build());
  }
}
