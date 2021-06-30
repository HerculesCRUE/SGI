package org.crue.hercules.sgi.csp.exceptions;

public class SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoEntidadFinanciadoraAjenaNotFoundException(Long solicitudProyectoEntidadFinanciadoraAjenaId) {
    super("SolicitudProyectoEntidadFinanciadoraAjena " + solicitudProyectoEntidadFinanciadoraAjenaId + " does not exist.");
  }
}
