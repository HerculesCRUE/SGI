package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.SolicitudRrhh;

public class SolicitudRrhhNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public SolicitudRrhhNotFoundException(Long id) {
    super(id, SolicitudRrhh.class);
  }

}
