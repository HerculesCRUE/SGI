package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoConvocatoriaReunionNotFoundException
 */
public class TipoConvocatoriaReunionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoConvocatoriaReunionNotFoundException(Long tipoConvocatoriaReunionId) {
    super("TipoConvocatoriaReunion " + tipoConvocatoriaReunionId + " does not exist.");
  }

}