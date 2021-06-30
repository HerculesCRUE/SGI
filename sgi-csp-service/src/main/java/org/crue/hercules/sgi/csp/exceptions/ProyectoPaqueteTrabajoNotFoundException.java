package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoPaqueteTrabajoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPaqueteTrabajoNotFoundException(Long proyectoPaqueteTrabajoId) {
    super("ProyectoPaqueteTrabajo " + proyectoPaqueteTrabajoId + " does not exist.");
  }

}
