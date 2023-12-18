package org.crue.hercules.sgi.sgdoc.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;

/**
 * SgdocNotFoundBySearchEntityException
 */
public class SgdocNotFoundBySearchEntityException extends NotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public SgdocNotFoundBySearchEntityException(String message) {
    super(message);
  }

}
