package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.Rango;

/**
 * RangoNotFoundException
 */
public class RangoNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public RangoNotFoundException(Long idRango) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Rango.class))
        .parameter("id", idRango).build());
  }
}
