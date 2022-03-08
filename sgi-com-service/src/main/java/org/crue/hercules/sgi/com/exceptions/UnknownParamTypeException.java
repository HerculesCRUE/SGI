package org.crue.hercules.sgi.com.exceptions;

import org.crue.hercules.sgi.com.enums.ParamType;

/**
 * Exception to throw when the specified {@link ParamType} is not known.
 */
public class UnknownParamTypeException extends RuntimeException {
  public UnknownParamTypeException(String paramType) {
    super(String.format("Unknown Param Type: %s", paramType));
  }
}
