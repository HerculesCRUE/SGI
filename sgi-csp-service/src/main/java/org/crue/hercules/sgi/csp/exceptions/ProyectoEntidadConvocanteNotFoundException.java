package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoEntidadConvocanteNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoEntidadConvocanteNotFoundException(Long proyectoEntidadConvocanteId) {
    super("ProyectoEntidadConvocante " + proyectoEntidadConvocanteId + " does not exist.");
  }
}
