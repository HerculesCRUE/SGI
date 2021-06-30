package org.crue.hercules.sgi.csp.exceptions;

public class TipoHitoNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public TipoHitoNotFoundException(Long tipoHitoId) {
    super("TipoHito " + tipoHitoId + " does not exist.");
  }

}
