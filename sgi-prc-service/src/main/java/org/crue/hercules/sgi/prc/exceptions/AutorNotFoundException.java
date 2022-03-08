package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.Autor;

/**
 * AutorNotFoundException
 */
public class AutorNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public AutorNotFoundException(Long idAutor) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Autor.class))
        .parameter("id", idAutor).build());
  }
}
