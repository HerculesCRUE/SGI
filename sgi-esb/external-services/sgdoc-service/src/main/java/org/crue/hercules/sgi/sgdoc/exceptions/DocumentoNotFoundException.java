package org.crue.hercules.sgi.sgdoc.exceptions;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.sgdoc.model.Documento;

/**
 * DocumentoNotFoundException
 */
public class DocumentoNotFoundException extends SgdocNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DocumentoNotFoundException(String documentoId) {
    super(ProblemMessage.builder().key(SgdocNotFoundException.class)
        .parameter("entity", ApplicationContextSupport.getMessage(
            Documento.class))
        .parameter("id", documentoId).build());
  }

}
