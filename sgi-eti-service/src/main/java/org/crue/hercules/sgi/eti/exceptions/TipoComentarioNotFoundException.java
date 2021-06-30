package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoComentarioNotFoundException
 */
public class TipoComentarioNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoComentarioNotFoundException(Long tipoComentarioId) {
    super("TipoComentario " + tipoComentarioId + " does not exist.");
  }

}