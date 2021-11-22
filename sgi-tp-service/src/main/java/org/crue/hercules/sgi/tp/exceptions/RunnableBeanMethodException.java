package org.crue.hercules.sgi.tp.exceptions;

public class RunnableBeanMethodException extends RuntimeException {

  public RunnableBeanMethodException() {
  }

  public RunnableBeanMethodException(String message) {
    super(message);
  }

  public RunnableBeanMethodException(Throwable cause) {
    super(cause);
  }

  public RunnableBeanMethodException(String message, Throwable cause) {
    super(message, cause);
  }

  public RunnableBeanMethodException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
