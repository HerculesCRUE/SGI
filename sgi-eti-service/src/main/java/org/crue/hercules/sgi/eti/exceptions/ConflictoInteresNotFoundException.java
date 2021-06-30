package org.crue.hercules.sgi.eti.exceptions;

/**
 * ConflictoInteresNotFoundException
 */
public class ConflictoInteresNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConflictoInteresNotFoundException(Long conflictoInteresId) {
    super("ConflictoInteres " + conflictoInteresId + " does not exist.");
  }

}