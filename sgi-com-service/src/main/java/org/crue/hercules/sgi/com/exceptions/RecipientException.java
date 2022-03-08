package org.crue.hercules.sgi.com.exceptions;

import org.crue.hercules.sgi.com.model.Email;
import org.crue.hercules.sgi.com.model.Recipient;

/**
 * Exception to throw when there is any problem with some {@link Email}
 * {@link Recipient}.
 */
public class RecipientException extends Exception {

  public RecipientException() {
  }

  public RecipientException(String message) {
    super(message);
  }

  public RecipientException(Throwable cause) {
    super(cause);
  }

  public RecipientException(String message, Throwable cause) {
    super(message, cause);
  }

  public RecipientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
