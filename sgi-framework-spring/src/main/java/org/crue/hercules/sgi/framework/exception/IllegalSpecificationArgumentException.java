package org.crue.hercules.sgi.framework.exception;

public class IllegalSpecificationArgumentException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public IllegalSpecificationArgumentException() {
  }

  public IllegalSpecificationArgumentException(String message) {
    super(message);
  }

  public IllegalSpecificationArgumentException(Throwable cause) {
    super(cause);
  }

  public IllegalSpecificationArgumentException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalSpecificationArgumentException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}