package org.crue.hercules.sgi.rel.exceptions;

import org.crue.hercules.sgi.framework.exception.NotFoundException;

/**
 * RelNotFoundException
 */
public class RelNotFoundException extends NotFoundException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public RelNotFoundException(String message) {
    super(message);
  }

}
