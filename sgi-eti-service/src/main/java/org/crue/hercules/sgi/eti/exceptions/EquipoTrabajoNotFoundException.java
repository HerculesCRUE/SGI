package org.crue.hercules.sgi.eti.exceptions;

/**
 * EquipoTrabajoNotFoundException
 */
public class EquipoTrabajoNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EquipoTrabajoNotFoundException(Long equipoTrabajoId) {
    super("EquipoTrabajo " + equipoTrabajoId + " does not exist.");
  }

}