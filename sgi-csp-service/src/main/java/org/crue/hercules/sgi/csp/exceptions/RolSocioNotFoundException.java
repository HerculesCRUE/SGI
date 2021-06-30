package org.crue.hercules.sgi.csp.exceptions;

public class RolSocioNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RolSocioNotFoundException(Long rolSocioId) {
    super("RolSocio " + rolSocioId + " does not exist.");
  }
}
