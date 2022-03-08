package org.crue.hercules.sgi.com.exceptions;

import javax.validation.constraints.Email;

/**
 * Exception to throw when there is any problem with the {@link Email} Content.
 */
public class ContentException extends Exception {

  public ContentException() {
  }

  public ContentException(String message) {
    super(message);
  }

  public ContentException(Throwable cause) {
    super(cause);
  }

  public ContentException(String message, Throwable cause) {
    super(message, cause);
  }

  public ContentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
