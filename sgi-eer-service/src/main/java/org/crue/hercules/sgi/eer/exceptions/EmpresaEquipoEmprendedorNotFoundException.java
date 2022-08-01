package org.crue.hercules.sgi.eer.exceptions;

import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;

public class EmpresaEquipoEmprendedorNotFoundException extends EerNotFoundException {

  /**
   * EmpresaEquipoEmprendedorNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public EmpresaEquipoEmprendedorNotFoundException(Long id) {
    super(ProblemMessage.builder().key(EerNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(EmpresaEquipoEmprendedor.class))
        .parameter("id", id).build());
  }
}
