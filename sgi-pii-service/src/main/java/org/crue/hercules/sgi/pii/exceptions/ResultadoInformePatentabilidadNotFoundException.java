package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class ResultadoInformePatentabilidadNotFoundException extends PiiNotFoundException {
  public static final String MSG_MODEL_RESULTADO_INFORME_PATENTABILIDAD = "org.crue.hercules.sgi.eti.model.ResultadoInformePatentabilidad.message";
  private static final long serialVersionUID = 1L;

  public ResultadoInformePatentabilidadNotFoundException(Long resultadoInformePatentabilidadId) {
    super(ApplicationContextSupport.getMessage("notFoundException",
        new Object[] { ApplicationContextSupport.getMessage(MSG_MODEL_RESULTADO_INFORME_PATENTABILIDAD),
            resultadoInformePatentabilidadId }));

  }
}
