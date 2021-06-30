package org.crue.hercules.sgi.eti.exceptions;

/**
 * RespuestaNotFoundException
 */
public class RespuestaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public RespuestaNotFoundException(Long respuestaId) {
    super("Respuesta " + respuestaId + " does not exist.");
  }

}