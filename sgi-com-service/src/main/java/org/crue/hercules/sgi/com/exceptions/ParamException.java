package org.crue.hercules.sgi.com.exceptions;

import javax.validation.constraints.Email;

import org.crue.hercules.sgi.com.model.Param;

/**
 * Exception to throw when there is any problem with some {@link Email}
 * {@link Param}.
 */
public class ParamException extends Exception {

  public ParamException() {
  }

  public ParamException(String message) {
    super(message);
  }

  public ParamException(Throwable cause) {
    super(cause);
  }

  public ParamException(String message, Throwable cause) {
    super(message, cause);
  }

  public ParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
