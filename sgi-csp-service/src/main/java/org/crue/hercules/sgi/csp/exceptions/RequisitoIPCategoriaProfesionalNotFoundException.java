package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.RequisitoIPCategoriaProfesional;

/**
 * public RequisitoIPCategoriaProfesionalNotFoundException(Long requisitoIPId) {
 * 
 */
public class RequisitoIPCategoriaProfesionalNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RequisitoIPCategoriaProfesionalNotFoundException(Long id) {
    super(id, RequisitoIPCategoriaProfesional.class);
  }

}
