package org.crue.hercules.sgi.eti.exceptions;

/**
 * EvaluacionNotFoundException
 */
public class EvaluacionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EvaluacionNotFoundException(Long evaluacionId) {
    super("Evaluacion " + evaluacionId + " does not exist.");
  }

}