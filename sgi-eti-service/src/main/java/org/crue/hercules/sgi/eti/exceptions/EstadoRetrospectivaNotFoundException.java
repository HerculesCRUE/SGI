package org.crue.hercules.sgi.eti.exceptions;

/**
 * EstadoRetrospectivaNotFoundException
 */
public class EstadoRetrospectivaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public EstadoRetrospectivaNotFoundException(Long estadoRetrospectivaId) {
    super("EstadoRetrospectiva " + estadoRetrospectivaId + " does not exist.");
  }

}