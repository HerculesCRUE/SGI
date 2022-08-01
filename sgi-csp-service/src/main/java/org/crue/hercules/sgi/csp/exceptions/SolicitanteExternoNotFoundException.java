package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.SolicitanteExterno;

public class SolicitanteExternoNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public SolicitanteExternoNotFoundException(Long id) {
    super(id, SolicitanteExterno.class);
  }

}
