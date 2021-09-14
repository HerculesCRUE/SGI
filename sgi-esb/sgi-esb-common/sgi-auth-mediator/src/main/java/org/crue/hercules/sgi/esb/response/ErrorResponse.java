package org.crue.hercules.sgi.esb.response;

/**
 * ErrorResponse
 */
public class ErrorResponse {

  public static final String UNAUTHORIZED = "Unauthorized";
  public static final String UNKNOWN = "Unknown";

  private String error;
  private String message;

  public ErrorResponse(String error, String message) {
    this.error = error;
    this.message = message;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
