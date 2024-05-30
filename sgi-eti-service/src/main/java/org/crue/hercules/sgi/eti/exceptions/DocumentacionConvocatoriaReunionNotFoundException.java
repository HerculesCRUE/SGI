package org.crue.hercules.sgi.eti.exceptions;

/**
 * DocumentacionConvocatoriaReunionNotFoundException
 */
public class DocumentacionConvocatoriaReunionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public DocumentacionConvocatoriaReunionNotFoundException(Long documentacionConvocatoriaReunionId) {
    super("DocumentacionConvocatoriaReunion " + documentacionConvocatoriaReunionId + " does not exist.");
  }

}