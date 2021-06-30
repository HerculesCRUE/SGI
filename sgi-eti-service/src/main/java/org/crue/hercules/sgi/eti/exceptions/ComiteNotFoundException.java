package org.crue.hercules.sgi.eti.exceptions;

/**
 * ComiteNotFoundException
 */
public class ComiteNotFoundException extends EtiNotFoundException {

  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public ComiteNotFoundException(Long comiteId) {
    super("Comité " + comiteId + " does not exist.");
  }
}