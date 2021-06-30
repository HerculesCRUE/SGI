package org.crue.hercules.sgi.eti.exceptions;

/**
 * EstadoMemoriaNotFoundException
 */
public class EstadoMemoriaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EstadoMemoriaNotFoundException(Long estadoMemoriaId) {
    super("EstadoMemoria " + estadoMemoriaId + " does not exist.");
  }

}