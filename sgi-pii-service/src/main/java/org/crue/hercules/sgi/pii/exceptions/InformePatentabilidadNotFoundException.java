package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.InformePatentabilidad;

public class InformePatentabilidadNotFoundException extends PiiNotFoundException {
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public InformePatentabilidadNotFoundException(Long informePatentabilidadId) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(InformePatentabilidad.class))
        .parameter("id", informePatentabilidadId).build());
  }
}
