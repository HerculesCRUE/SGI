package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.TipoProteccion;

public class TipoProteccionNotFoundException extends PiiNotFoundException {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoProteccionNotFoundException(Long sectorAplicacionId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(TipoProteccion.class))
        .parameter("id", sectorAplicacionId).build());
  }
}
