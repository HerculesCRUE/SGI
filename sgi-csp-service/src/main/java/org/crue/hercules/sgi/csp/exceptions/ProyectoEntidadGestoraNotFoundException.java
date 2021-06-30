package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoEntidadGestoraNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoEntidadGestoraNotFoundException(Long proyectoEntidadGestoraId) {
    super("ProyectoEntidadGestora " + proyectoEntidadGestoraId + " does not exist.");
  }
}
