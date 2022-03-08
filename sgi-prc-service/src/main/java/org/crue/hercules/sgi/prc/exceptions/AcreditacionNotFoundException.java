package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.Acreditacion;

/**
 * AcreditacionNotFoundException
 */
public class AcreditacionNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public AcreditacionNotFoundException(Long idAcreditacion) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Acreditacion.class))
        .parameter("id", idAcreditacion).build());
  }
}
