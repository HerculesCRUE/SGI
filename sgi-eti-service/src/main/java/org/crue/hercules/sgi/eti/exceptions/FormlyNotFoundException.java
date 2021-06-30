package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.eti.model.Formly;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * Excepción lanzada cuando no existe el Formly solicitado.
 */
public class FormlyNotFoundException extends EtiNotFoundException {

  public FormlyNotFoundException(Long id) {
    super(ProblemMessage.builder().key(EtiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Formly.class))
        .parameter("field", ApplicationContextSupport.getMessage("id")).parameter("value", id).build());
  }

  public FormlyNotFoundException(String nombre) {
    super(ProblemMessage.builder().key(EtiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(Formly.class))
        .parameter("field", ApplicationContextSupport.getMessage("nombre")).parameter("value", nombre).build());
  }
}
