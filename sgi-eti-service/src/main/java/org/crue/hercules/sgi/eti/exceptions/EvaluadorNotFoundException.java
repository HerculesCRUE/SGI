package org.crue.hercules.sgi.eti.exceptions;

/**
 * EvaluadorNotFoundException
 */
public class EvaluadorNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EvaluadorNotFoundException(Long evaluadorId) {
    super("Evaluador " + evaluadorId + " does not exist.");
  }

}