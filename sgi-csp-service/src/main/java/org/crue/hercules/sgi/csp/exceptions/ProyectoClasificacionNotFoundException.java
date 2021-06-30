package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoClasificacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoClasificacionNotFoundException(Long proyectoClasificacionId) {
    super("ProyectoClasificacion " + proyectoClasificacionId + " does not exist.");
  }
}
