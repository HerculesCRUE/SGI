package org.crue.hercules.sgi.tp.exceptions;

public class UnknownServiceExtension extends RuntimeException {
  public UnknownServiceExtension(String serviceType) {
    super(String.format("Unknown Service Type: %s", serviceType));
  }
}
