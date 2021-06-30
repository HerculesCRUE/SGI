package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;

/**
 * PiiNotFoundException
 */
public class PiiNotFoundException extends NotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public PiiNotFoundException(String message) {
    super(message);
  }

}
