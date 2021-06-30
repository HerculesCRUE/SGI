package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoEquipoNotFoundException
 */
public class ProyectoEquipoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoEquipoNotFoundException(Long proyectoEquipoId) {
    super("Proyecto equipo " + proyectoEquipoId + " does not exist.");
  }

}
