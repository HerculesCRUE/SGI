package org.crue.hercules.sgi.com.exceptions;

import org.crue.hercules.sgi.com.model.Email;

/**
 * Exception to throw when there is any problem with the {@link Email} Subject.
 */
public class SubjectException extends Exception {

  public SubjectException() {
  }

  public SubjectException(String message) {
    super(message);
  }

  public SubjectException(Throwable cause) {
    super(cause);
  }

  public SubjectException(String message, Throwable cause) {
    super(message, cause);
  }

  public SubjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
