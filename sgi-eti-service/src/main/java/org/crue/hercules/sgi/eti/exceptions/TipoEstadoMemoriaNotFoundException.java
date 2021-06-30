package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoEstadoMemoriaNotFoundException
 */
public class TipoEstadoMemoriaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoEstadoMemoriaNotFoundException(Long tipoEstadoMemoriaId) {
    super("TipoEstadoMemoria " + tipoEstadoMemoriaId + " does not exist.");
  }

}