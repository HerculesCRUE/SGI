package org.crue.hercules.sgi.eti.exceptions;

import org.crue.hercules.sgi.eti.enums.ServiceType;

/**
 * Exception to throw when the specified {@link ServiceType} is not known.
 */
public class UnknownServiceTypeException extends RuntimeException {
  public UnknownServiceTypeException(String serviceType) {
    super(String.format("Unknown Service Type: %s", serviceType));
  }
}
