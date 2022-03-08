package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.AliasEnumerado;

/**
 * AliasEnumeradoNotFoundException
 */
public class AliasEnumeradoNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public AliasEnumeradoNotFoundException(Long idAliasEnumerado) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(AliasEnumerado.class))
        .parameter("id", idAliasEnumerado).build());
  }
}
