package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * FormacionEspecificaNotFoundException
 */
public class FormacionEspecificaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_FORMACION_ESPECIFICA = "org.crue.hercules.sgi.eti.model.FormacionEspecifica.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public FormacionEspecificaNotFoundException(Long formacionEspecificaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_FORMACION_ESPECIFICA), formacionEspecificaId }));
  }

}