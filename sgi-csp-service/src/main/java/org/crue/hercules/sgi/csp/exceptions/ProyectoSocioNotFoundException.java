package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoSocioNotFoundException
 */
public class ProyectoSocioNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoSocioNotFoundException(Long proyectoSocioId) {
    super("ProyectoSocio " + proyectoSocioId + " does not exist.");
  }

}
