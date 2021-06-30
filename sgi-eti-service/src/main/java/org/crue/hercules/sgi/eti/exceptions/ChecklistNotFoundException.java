package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.eti.model.Checklist;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * Excepción lanzada cuando no existe el Checklist solicitado.
 */
public class ChecklistNotFoundException extends EtiNotFoundException {

  public ChecklistNotFoundException(Long id) {
    super(ProblemMessage.builder().key(EtiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Checklist.class))
        .parameter("field", ApplicationContextSupport.getMessage("id")).parameter("value", id).build());
  }
}
