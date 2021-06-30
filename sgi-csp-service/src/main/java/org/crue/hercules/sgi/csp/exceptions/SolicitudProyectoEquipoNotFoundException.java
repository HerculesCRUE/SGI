package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoNotFoundException
 */
public class SolicitudProyectoEquipoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoEquipoNotFoundException(Long solicitudProyectoEquipoId) {
    super("Solicitud proyecto equipo " + solicitudProyectoEquipoId + " does not exist.");
  }

}
