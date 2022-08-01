package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;

/**
 * public RequisitoIPNivelAcademicoNotFoundException(Long requisitoIPId) {
 * 
 */
public class RequisitoIPNivelAcademicoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RequisitoIPNivelAcademicoNotFoundException(Long id) {
    super(id, RequisitoIPNivelAcademico.class);
  }

}
