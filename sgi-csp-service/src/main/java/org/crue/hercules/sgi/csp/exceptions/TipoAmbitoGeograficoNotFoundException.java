package org.crue.hercules.sgi.csp.exceptions;

/**
 * TipoAmbitoGeograficoNotFoundException
 */
public class TipoAmbitoGeograficoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoAmbitoGeograficoNotFoundException(Long tipoAmbitoGeograficoId) {
    super("TipoAmbitoGeografico " + tipoAmbitoGeograficoId + " does not exist.");
  }
}
