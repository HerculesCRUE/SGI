package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoAnualidadNotFoundException
 */
public class ProyectoAnualidadNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoAnualidadNotFoundException(Long proyectoAnualidadId) {
    super("ProyectoAnualidad " + proyectoAnualidadId + " does not exist.");
  }

}
