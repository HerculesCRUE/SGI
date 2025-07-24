package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * FormularioNotFoundException
 */
public class FormularioNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_FORMULARIO = "org.crue.hercules.sgi.eti.model.Formulario.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public FormularioNotFoundException(Long formularioId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_FORMULARIO), formularioId }));
  }

}