package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoFaseNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoFaseNotFoundException(Long proyectoFaseId) {
    super("ProyectoFase " + proyectoFaseId + " does not exist.");
  }

}
