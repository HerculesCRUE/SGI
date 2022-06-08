package org.crue.hercules.sgi.pii.exceptions;

import org.crue.hercules.sgi.pii.enums.ServiceType;

/**
 * Exception to throw when the specified {@link ServiceType} is not known.
 */
public class UnknownServiceTypeException extends RuntimeException {
  public UnknownServiceTypeException(String serviceType) {
    super(String.format("Unknown Service Type: %s", serviceType));
  }
}
