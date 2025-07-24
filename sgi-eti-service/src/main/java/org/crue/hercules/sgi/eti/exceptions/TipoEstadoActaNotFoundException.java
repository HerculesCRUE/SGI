package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoEstadoActaNotFoundException
 */
public class TipoEstadoActaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_ESTADO_ACTA = "org.crue.hercules.sgi.eti.model.TipoEstadoActa.message";
  /**
   * Serial Version.
   */
  private static final long serialVersionUID = 1L;

  public TipoEstadoActaNotFoundException(Long tipoEstadoActaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_ESTADO_ACTA), tipoEstadoActaId }));
  }

}