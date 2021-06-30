package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoEntidadFinanciadoraNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoEntidadFinanciadoraNotFoundException(Long proyectoEntidadFinanciadoraId) {
    super("ProyectoEntidadFinanciadora " + proyectoEntidadFinanciadoraId + " doesn't exist.");
  }
}
