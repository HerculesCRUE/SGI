package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.AutorGrupo;

/**
 * AutorGrupoNotFoundException
 */
public class AutorGrupoNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public AutorGrupoNotFoundException(Long idAutorGrupo) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(AutorGrupo.class))
        .parameter("id", idAutorGrupo).build());
  }
}
