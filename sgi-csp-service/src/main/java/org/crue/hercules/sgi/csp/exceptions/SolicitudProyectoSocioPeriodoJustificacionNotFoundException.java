package org.crue.hercules.sgi.csp.exceptions;

/**
 * SolicitudProyectoSocioPeriodoJustificacionNotFoundException
 */
public class SolicitudProyectoSocioPeriodoJustificacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoSocioPeriodoJustificacionNotFoundException(
      Long solicitudProyectoSocioPeriodoJustificacionId) {
    super("Solicitud proyecto periodo justificación " + solicitudProyectoSocioPeriodoJustificacionId
        + " does not exist.");
  }

}
