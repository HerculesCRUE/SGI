package org.crue.hercules.sgi.csp.exceptions;

public class TipoFaseNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public TipoFaseNotFoundException(Long tipoFaseId) {
    super("TipoFase " + tipoFaseId + " does not exist.");
  }

}
