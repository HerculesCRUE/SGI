package org.crue.hercules.sgi.eti.exceptions;

/**
 * ConvocatoriaReunionNotFoundException
 */
public class ConvocatoriaReunionNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaReunionNotFoundException(Long convocatoriaReunionId) {
    super("ConvocatoriaReunion " + convocatoriaReunionId + " does not exist.");
  }

}