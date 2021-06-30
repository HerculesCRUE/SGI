package org.crue.hercules.sgi.csp.exceptions;

public class TipoFinanciacionNotFoundException extends CspNotFoundException {

  private static final long serialVersionUID = 1L;

  public TipoFinanciacionNotFoundException(Long tipoFinanciacionId) {
    super("TipoFinanciacion " + tipoFinanciacionId + " does not exist.");

  }
}
