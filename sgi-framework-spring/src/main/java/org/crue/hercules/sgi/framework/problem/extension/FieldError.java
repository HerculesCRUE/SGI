package org.crue.hercules.sgi.framework.problem.extension;

import java.io.Serializable;

/**
 * {@link org.crue.hercules.sgi.framework.problem.Problem} extension to support
 * error on class fields.
 */
public class FieldError implements Serializable {
  private static final long serialVersionUID = 1L;

  /** The field containing the error */
  private final String field;
  /** The error description */
  private final String error;

  /**
   * Builds a new FieldError
   * 
   * @param field the field containing the error
   * @param error the error description
   */
  public FieldError(String field, String error) {
    this.field = field;
    this.error = error;
  }

  /**
   * Returns the field name
   * 
   * @return the field name
   */
  public String getField() {
    return field;
  }

  /**
   * Returns the error message
   * 
   * @return the error message
   */
  public String getError() {
    return error;
  }
}
