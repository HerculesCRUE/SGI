package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.ViaProteccion;

public class ViaProteccionNotFoundException extends PiiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ViaProteccionNotFoundException(Long viaProteccionId) {

    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(ViaProteccion.class)).parameter("id", viaProteccionId)
        .build());
  }
}
