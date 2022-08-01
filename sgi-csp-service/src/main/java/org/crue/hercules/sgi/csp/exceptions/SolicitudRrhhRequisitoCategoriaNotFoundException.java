package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;

public class SolicitudRrhhRequisitoCategoriaNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public SolicitudRrhhRequisitoCategoriaNotFoundException(Long id) {
    super(id, SolicitudRrhhRequisitoCategoria.class);
  }

}
