package org.crue.hercules.sgi.eti.exceptions;

/**
 * CargoComiteNotFoundException
 */
public class CargoComiteNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public CargoComiteNotFoundException(Long cargoComiteId) {
    super("CargoComite " + cargoComiteId + " does not exist.");
  }

}