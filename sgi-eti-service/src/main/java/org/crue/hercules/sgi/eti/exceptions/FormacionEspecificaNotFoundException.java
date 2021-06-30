package org.crue.hercules.sgi.eti.exceptions;

/**
 * FormacionEspecificaNotFoundException
 */
public class FormacionEspecificaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public FormacionEspecificaNotFoundException(Long formacionEspecificaId) {
    super("FormacionEspecifica " + formacionEspecificaId + " does not exist.");
  }

}