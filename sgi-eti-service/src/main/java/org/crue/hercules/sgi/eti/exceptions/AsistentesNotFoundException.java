package org.crue.hercules.sgi.eti.exceptions;

/**
 * AsistentesNotFoundException
 */
public class AsistentesNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public AsistentesNotFoundException(Long asistentesId) {
    super("Asistentes " + asistentesId + " does not exist.");
  }

}