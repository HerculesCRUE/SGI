package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.model.Proyecto;

/**
 * ProyectoNotFoundException
 */
public class ProyectoNotFoundException extends PrcNotFoundException {

  private static final long serialVersionUID = 1L;

  public ProyectoNotFoundException(Long idProyecto) {
    super(ProblemMessage.builder().key(PrcNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Proyecto.class))
        .parameter("id", idProyecto).build());
  }
}
