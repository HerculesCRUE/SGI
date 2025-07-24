package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoConvocatoriaReunionNotFoundException
 */
public class TipoConvocatoriaReunionNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_CONVOCATORIA_REUNION = "org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoConvocatoriaReunionNotFoundException(Long tipoConvocatoriaReunionId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_CONVOCATORIA_REUNION),
            tipoConvocatoriaReunionId }));
  }

}