package org.crue.hercules.sgi.prc.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;

/**
 * PrcNotFoundException
 */
public class PrcNotFoundException extends NotFoundException {

  private static final long serialVersionUID = 1L;

  public PrcNotFoundException(String message) {
    super(message);
  }

}
