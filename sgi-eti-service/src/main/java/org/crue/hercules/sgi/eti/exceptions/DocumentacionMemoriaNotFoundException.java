package org.crue.hercules.sgi.eti.exceptions;

/**
 * DocumentacionMemoriaNotFoundException
 */
public class DocumentacionMemoriaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public DocumentacionMemoriaNotFoundException(Long documentacionMemoriaId) {
    super("DocumentacionMemoria " + documentacionMemoriaId + " does not exist.");
  }

}