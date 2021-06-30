package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoHitoNotFoundException extends CspNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ProyectoHitoNotFoundException(Long ProyectoHitoId) {
    super("ProyectoHito " + ProyectoHitoId + " does not exist.");
  }

}
