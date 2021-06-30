package org.crue.hercules.sgi.eti.exceptions;

/**
 * FormularioNotFoundException
 */
public class FormularioNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public FormularioNotFoundException(Long formularioId) {
    super("Formulario " + formularioId + " does not exist.");
  }

}