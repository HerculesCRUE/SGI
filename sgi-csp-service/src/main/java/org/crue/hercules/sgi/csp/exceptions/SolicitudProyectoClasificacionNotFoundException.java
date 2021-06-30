package org.crue.hercules.sgi.csp.exceptions;

public class SolicitudProyectoClasificacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SolicitudProyectoClasificacionNotFoundException(Long solicitudProyectoClasificacionId) {
    super("SolicitudProyectoClasificacion " + solicitudProyectoClasificacionId + " does not exist.");
  }
}
