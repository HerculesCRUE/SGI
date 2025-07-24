package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ConvocatoriaEntidadFinanciadoraNotFoundException extends CspNotFoundException {
  public static final String MSG_MODEL_CONVOCATORIA_ENTIDAD_FINANCIADORA = "org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora.message";
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaEntidadFinanciadoraNotFoundException(Long convocatoriaEntidadFinanciadoraId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_CONVOCATORIA_ENTIDAD_FINANCIADORA),
            convocatoriaEntidadFinanciadoraId }));
  }
}
