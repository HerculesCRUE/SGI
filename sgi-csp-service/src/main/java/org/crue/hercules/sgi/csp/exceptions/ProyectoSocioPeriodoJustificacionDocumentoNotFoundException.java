package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoSocioPeriodoJustificacionDocumentoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioPeriodoJustificacionDocumentoNotFoundException(
      Long proyectoSocioPeriodoJustificacionDocumentoId) {
    super("Socio periodo justificación documento " + proyectoSocioPeriodoJustificacionDocumentoId + " does not exist.");
  }
}
