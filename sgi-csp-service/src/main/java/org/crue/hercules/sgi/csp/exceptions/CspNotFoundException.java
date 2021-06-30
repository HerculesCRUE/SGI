package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;

/**
 * CspNotFoundException
 */
public class CspNotFoundException extends NotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public CspNotFoundException(String message) {
    super(message);
  }

}
