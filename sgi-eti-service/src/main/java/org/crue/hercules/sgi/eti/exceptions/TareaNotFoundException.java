package org.crue.hercules.sgi.eti.exceptions;

/**
 * TareaNotFoundException
 */
public class TareaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TareaNotFoundException(Long tareaId) {
    super("Tarea " + tareaId + " does not exist.");
  }

}