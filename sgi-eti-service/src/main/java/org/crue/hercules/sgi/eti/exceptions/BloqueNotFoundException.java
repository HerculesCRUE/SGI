package org.crue.hercules.sgi.eti.exceptions;

/**
 * BloqueNotFoundException
 */
public class BloqueNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public BloqueNotFoundException(Long bloqueId) {
    super("Bloque " + bloqueId + " does not exist.");
  }

}