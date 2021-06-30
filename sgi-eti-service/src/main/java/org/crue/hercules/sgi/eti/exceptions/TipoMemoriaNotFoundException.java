package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoMemoriaNotFoundException
 */
public class TipoMemoriaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoMemoriaNotFoundException(Long tipoMemoriaId) {
    super("TipoMemoria " + tipoMemoriaId + " does not exist.");
  }

}