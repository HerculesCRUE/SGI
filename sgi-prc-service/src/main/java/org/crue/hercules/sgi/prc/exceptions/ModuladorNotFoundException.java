package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.Modulador;

/**
 * ModuladorNotFoundException
 */
public class ModuladorNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public ModuladorNotFoundException(Long idModulador) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Modulador.class))
        .parameter("id", idModulador).build());
  }
}
