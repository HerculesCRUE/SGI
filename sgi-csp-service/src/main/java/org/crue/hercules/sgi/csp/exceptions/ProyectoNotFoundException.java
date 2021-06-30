package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoNotFoundException
 */
public class ProyectoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoNotFoundException(Long proyectoId) {
    super("Proyecto " + proyectoId + " does not exist.");
  }

}
