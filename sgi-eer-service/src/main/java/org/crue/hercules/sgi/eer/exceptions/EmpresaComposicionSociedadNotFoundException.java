package org.crue.hercules.sgi.eer.exceptions;

import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class EmpresaComposicionSociedadNotFoundException extends EerNotFoundException {

  /**
   * EmpresaComposicionSociedadNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public EmpresaComposicionSociedadNotFoundException(Long id) {
    super(ProblemMessage.builder().key(EerNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(EmpresaComposicionSociedad.class))
        .parameter("id", id).build());
  }
}
