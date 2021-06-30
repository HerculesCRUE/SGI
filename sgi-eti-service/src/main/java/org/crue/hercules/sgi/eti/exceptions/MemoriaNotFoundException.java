package org.crue.hercules.sgi.eti.exceptions;

/**
 * MemoriaNotFoundException
 */
public class MemoriaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public MemoriaNotFoundException(Long memoriaId) {
    super("Memoria " + memoriaId + " does not exist.");
  }

}