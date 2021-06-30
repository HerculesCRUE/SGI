package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoProrrogaNotFoundException extends CspNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ProyectoProrrogaNotFoundException(Long ProyectoProrrogaId) {
    super("ProyectoProrroga " + ProyectoProrrogaId + " does not exist.");
  }

}
