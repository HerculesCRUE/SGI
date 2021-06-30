package org.crue.hercules.sgi.eti.exceptions;

/**
 * InformeNotFoundException
 */
public class InformeNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public InformeNotFoundException(Long informeId) {
    super("Informe " + informeId + " does not exist.");
  }

}