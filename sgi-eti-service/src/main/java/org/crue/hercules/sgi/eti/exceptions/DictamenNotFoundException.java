package org.crue.hercules.sgi.eti.exceptions;

/**
 * DictamenNotFoundException
 */
public class DictamenNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public DictamenNotFoundException(Long dictamenId) {
    super("Dictamen " + dictamenId + " does not exist.");
  }

}