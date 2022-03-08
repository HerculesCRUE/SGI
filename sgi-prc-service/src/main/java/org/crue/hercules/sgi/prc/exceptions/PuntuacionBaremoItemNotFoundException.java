package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.PuntuacionBaremoItem;

/**
 * PuntuacionBaremoItemNotFoundException
 */
public class PuntuacionBaremoItemNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public PuntuacionBaremoItemNotFoundException(Long puntuacionBaremoItemId) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(PuntuacionBaremoItem.class))
        .parameter("id", puntuacionBaremoItemId).build());
  }
}
