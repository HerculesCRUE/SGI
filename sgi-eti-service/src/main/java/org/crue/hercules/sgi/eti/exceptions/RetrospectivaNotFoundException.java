package org.crue.hercules.sgi.eti.exceptions;

/**
 * RetrospectivaNotFoundException
 */
public class RetrospectivaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public RetrospectivaNotFoundException(Long retrospectivaId) {
    super("Retrospectiva " + retrospectivaId + " does not exist.");
  }

}