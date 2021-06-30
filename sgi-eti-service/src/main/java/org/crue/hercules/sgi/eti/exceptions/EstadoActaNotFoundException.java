package org.crue.hercules.sgi.eti.exceptions;

/**
 * EstadoActaNotFoundException
 */
public class EstadoActaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EstadoActaNotFoundException(Long estadoActaId) {
    super("EstadoActa " + estadoActaId + " does not exist.");
  }

}