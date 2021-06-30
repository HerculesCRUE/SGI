package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoMemoriaComiteNotFoundException
 */
public class TipoMemoriaComiteNotFoundException extends EtiNotFoundException {
  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public TipoMemoriaComiteNotFoundException(Long tipoMemoriaComiteId) {
    super("TipoMemoriaComite " + tipoMemoriaComiteId + " does not exist.");
  }
}