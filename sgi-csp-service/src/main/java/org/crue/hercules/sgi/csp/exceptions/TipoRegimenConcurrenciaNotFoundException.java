package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class TipoRegimenConcurrenciaNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_TIPO_REGIMEN_CONCURRENCIA = "org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoRegimenConcurrenciaNotFoundException(Long tipoRegimenConcurrenciaId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_TIPO_REGIMEN_CONCURRENCIA),
            tipoRegimenConcurrenciaId }));
  }
}
