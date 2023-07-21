package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.RolSocio;

public class RolSocioNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RolSocioNotFoundException(Long rolSocioId) {
    super(rolSocioId, RolSocio.class);
  }
}
