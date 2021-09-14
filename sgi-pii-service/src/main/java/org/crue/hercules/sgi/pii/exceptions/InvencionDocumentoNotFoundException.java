package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.model.InvencionDocumento;

public class InvencionDocumentoNotFoundException extends PiiNotFoundException {

  public InvencionDocumentoNotFoundException(Long id) {
    super(ProblemMessage.builder().key(PiiNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(InvencionDocumento.class)).parameter("id", id)
        .build());
  }

}
