package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.AgrupacionGastoConcepto;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

/**
 * AgrupacionGastoConceptoNotFoundException
 */
public class AgrupacionGastoConceptoNotFoundException extends CspNotFoundException {

  /** 
   *
   */
  private static final long serialVersionUID = 1L;

  public AgrupacionGastoConceptoNotFoundException(Long agrupacionGastoConceptoId) {
    super(ProblemMessage.builder().key(CspNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(AgrupacionGastoConcepto.class))
        .parameter("id", agrupacionGastoConceptoId).build());
  }
}
