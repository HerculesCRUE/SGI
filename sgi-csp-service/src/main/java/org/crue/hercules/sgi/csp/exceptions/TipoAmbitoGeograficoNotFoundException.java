package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;

/**
 * TipoAmbitoGeograficoNotFoundException
 */
public class TipoAmbitoGeograficoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoAmbitoGeograficoNotFoundException(Long tipoAmbitoGeograficoId) {
    super(tipoAmbitoGeograficoId, TipoAmbitoGeografico.class);
  }
}
