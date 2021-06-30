package org.crue.hercules.sgi.eti.exceptions;

/**
 * ComponenteFormularioNotFoundException
 */
public class ComponenteFormularioNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ComponenteFormularioNotFoundException(Long componenteFormularioId) {
    super("ComponenteFormulario " + componenteFormularioId + " does not exist.");
  }

}