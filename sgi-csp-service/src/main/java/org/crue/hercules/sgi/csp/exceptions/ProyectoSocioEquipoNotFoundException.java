package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoSocioEquipoNotFoundException
 */
public class ProyectoSocioEquipoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioEquipoNotFoundException(Long proyectoSocioEquipoId) {
    super("Proyecto socio equipo" + proyectoSocioEquipoId + " does not exist.");
  }

}
