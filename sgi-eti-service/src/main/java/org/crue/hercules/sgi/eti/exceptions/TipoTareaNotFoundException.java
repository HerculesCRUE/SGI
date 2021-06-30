package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoTareaNotFoundException
 */
public class TipoTareaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoTareaNotFoundException(Long tipoTareaId) {
    super("TipoTarea " + tipoTareaId + " does not exist.");
  }

}