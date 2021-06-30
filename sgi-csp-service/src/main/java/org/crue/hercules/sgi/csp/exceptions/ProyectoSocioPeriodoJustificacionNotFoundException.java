package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoSocioPeriodoJustificacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioPeriodoJustificacionNotFoundException(Long proyectoSocioPeriodoJustificacionId) {
    super("Proyecto socio periodo de justificación " + proyectoSocioPeriodoJustificacionId + " does not exist.");
  }
}
