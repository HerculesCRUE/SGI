package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoEstadoActaNotFoundException
 */
public class TipoEstadoActaNotFoundException extends EtiNotFoundException {

  /**
   * Serial Version.
   */
  private static final long serialVersionUID = 1L;

  public TipoEstadoActaNotFoundException(Long tipoEstadoActaId) {
    super("TipoEstadoActa " + tipoEstadoActaId + " does not exist.");
  }

}