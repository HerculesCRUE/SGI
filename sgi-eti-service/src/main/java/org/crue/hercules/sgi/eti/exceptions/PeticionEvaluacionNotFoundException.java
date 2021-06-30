package org.crue.hercules.sgi.eti.exceptions;

/**
 * PeticionEvaluacionNotFoundException
 */
public class PeticionEvaluacionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public PeticionEvaluacionNotFoundException(Long peticionEvaluacionId) {
    super("PeticionEvaluacion " + peticionEvaluacionId + " does not exist.");
  }

}