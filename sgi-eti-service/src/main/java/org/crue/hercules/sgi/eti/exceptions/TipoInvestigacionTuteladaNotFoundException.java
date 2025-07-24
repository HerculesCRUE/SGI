package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * TipoInvestigacionTuteladaNotFoundException
 */
public class TipoInvestigacionTuteladaNotFoundException extends EtiNotFoundException {
  public static final String MSG_MODEL_TIPO_INVESTIGACION_TUTELADA = "org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada.message";
  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoInvestigacionTuteladaNotFoundException(Long tipoInvestigacionTuteladaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_INVESTIGACION_TUTELADA),
            tipoInvestigacionTuteladaId }));
  }

}