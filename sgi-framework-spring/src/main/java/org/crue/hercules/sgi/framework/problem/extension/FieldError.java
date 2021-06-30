package org.crue.hercules.sgi.framework.problem.extension;

import java.io.Serializable;

public class FieldError implements Serializable {
  private static final long serialVersionUID = 1L;

  private final String field;
  private final String error;

  public FieldError(String field, String error) {
    this.field = field;
    this.error = error;
  }

  public String getField() {
    return field;
  }

  public String getError() {
    return error;
  }
}
