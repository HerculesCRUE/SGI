package org.crue.hercules.sgi.eer.exceptions;

import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class EmpresaAdministracionSociedadNotFoundException extends EerNotFoundException {

  /**
   * EmpresaAdministracionSociedadNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public EmpresaAdministracionSociedadNotFoundException(Long id) {
    super(ProblemMessage.builder().key(EerNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(EmpresaAdministracionSociedad.class))
        .parameter("id", id).build());
  }
}
