package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoIVANotFoundException extends CspNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ProyectoIVANotFoundException(Long ProyectoIVAId) {
    super("ProyectoIVA " + ProyectoIVAId + " does not exist.");
  }

}
