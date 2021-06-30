package org.crue.hercules.sgi.csp.exceptions;

/**
 * RequisitoIPNotFoundException
 */
public class RequisitoIPNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RequisitoIPNotFoundException(Long requisitoIPId) {
    super("RequisitoIP " + requisitoIPId + " does not exist.");
  }

}
