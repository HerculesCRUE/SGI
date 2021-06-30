package org.crue.hercules.sgi.eti.exceptions;

/**
 * ApartadoNotFoundException
 */
public class ApartadoNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ApartadoNotFoundException(Long apartadoId) {
    super("Apartado " + apartadoId + " does not exist.");
  }

}