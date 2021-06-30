package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;

/**
 * EtiServiceNotFoundException
 */
public class EtiNotFoundException extends NotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public EtiNotFoundException(String message) {
    super(message);
  }

}