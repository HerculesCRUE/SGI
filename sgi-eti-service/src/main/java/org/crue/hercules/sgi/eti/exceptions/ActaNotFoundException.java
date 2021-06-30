package org.crue.hercules.sgi.eti.exceptions;

/**
 * ActaNotFoundException
 */
public class ActaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ActaNotFoundException(Long actaId) {
    super("Acta " + actaId + " does not exist.");
  }

}