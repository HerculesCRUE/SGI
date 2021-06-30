package org.crue.hercules.sgi.framework.exception;

public class UnsupportedSpecificationOperationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public UnsupportedSpecificationOperationException() {
  }

  public UnsupportedSpecificationOperationException(String message) {
    super(message);
  }

  public UnsupportedSpecificationOperationException(Throwable cause) {
    super(cause);
  }

  public UnsupportedSpecificationOperationException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnsupportedSpecificationOperationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}