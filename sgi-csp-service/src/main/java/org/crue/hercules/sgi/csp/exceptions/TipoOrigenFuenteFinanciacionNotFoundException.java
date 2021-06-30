package org.crue.hercules.sgi.csp.exceptions;

/**
 * TipoOrigenFuenteFinanciacionNotFoundException
 */
public class TipoOrigenFuenteFinanciacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoOrigenFuenteFinanciacionNotFoundException(Long tipoOrigenFuenteFinanciacionId) {
    super("TipoOrigenFuenteFinanciacion " + tipoOrigenFuenteFinanciacionId + " does not exist.");
  }
}
