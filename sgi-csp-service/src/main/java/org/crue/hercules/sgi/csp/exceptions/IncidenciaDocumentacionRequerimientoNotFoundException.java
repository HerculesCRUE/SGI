package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * IncidenciaDocumentacionRequerimientoNotFoundException
 */
public class IncidenciaDocumentacionRequerimientoNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public IncidenciaDocumentacionRequerimientoNotFoundException(Long incidenciaDocumentacionRequerimientoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(IncidenciaDocumentacionRequerimiento.class))
        .parameter("id", incidenciaDocumentacionRequerimientoId).build());
  }
}
