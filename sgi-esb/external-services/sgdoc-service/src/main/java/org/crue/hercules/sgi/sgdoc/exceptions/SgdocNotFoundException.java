package org.crue.hercules.sgi.sgdoc.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;

/**
 * SgdocNotFoundException
 */
public class SgdocNotFoundException extends NotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public SgdocNotFoundException(String message) {
    super(message);
  }

}
