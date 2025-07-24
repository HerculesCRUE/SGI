package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * ComponenteFormularioNotFoundException
 */
public class ComponenteFormularioNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_COMPONENTE_FORMULARIO = "org.crue.hercules.sgi.eti.model.ComponenteFormulario.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ComponenteFormularioNotFoundException(Long componenteFormularioId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(
            MSG_MODEL_COMPONENTE_FORMULARIO), componenteFormularioId }));
  }

}