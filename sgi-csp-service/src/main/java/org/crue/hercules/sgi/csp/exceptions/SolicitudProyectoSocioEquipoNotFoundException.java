package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoSocioEquipoNotFoundException
 */
public class SolicitudProyectoSocioEquipoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoSocioEquipoNotFoundException(Long solicitudProyectoEquipoSocioId) {
    super("Solicitud proyecto equipo socio " + solicitudProyectoEquipoSocioId + " does not exist.");
  }

}
