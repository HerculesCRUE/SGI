package org.crue.hercules.sgi.eti.exceptions;

/**
 * ComentarioNotFoundException
 */
public class ComentarioNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ComentarioNotFoundException(Long comentarioId) {
    super("Comentario " + comentarioId + " does not exist.");
  }

}