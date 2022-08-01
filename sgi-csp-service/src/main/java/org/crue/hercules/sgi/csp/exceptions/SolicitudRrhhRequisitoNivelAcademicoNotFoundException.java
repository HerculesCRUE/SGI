package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico;

public class SolicitudRrhhRequisitoNivelAcademicoNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public SolicitudRrhhRequisitoNivelAcademicoNotFoundException(Long id) {
    super(id, SolicitudRrhhRequisitoNivelAcademico.class);
  }

}
