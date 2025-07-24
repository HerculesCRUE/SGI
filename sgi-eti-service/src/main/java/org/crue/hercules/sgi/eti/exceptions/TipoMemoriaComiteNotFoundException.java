package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoMemoriaComiteNotFoundException
 */
public class TipoMemoriaComiteNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_MEMORIA_COMITE = "org.crue.hercules.sgi.eti.model.TipoMemoriaComite.message";
  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public TipoMemoriaComiteNotFoundException(Long tipoMemoriaComiteId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_MEMORIA_COMITE), tipoMemoriaComiteId }));
  }
}