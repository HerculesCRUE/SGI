package org.crue.hercules.sgi.csp.exceptions;

public class TipoRegimenConcurrenciaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public TipoRegimenConcurrenciaNotFoundException(Long tipoRegimenConcurrenciaId) {
    super("TipoRegimenConcurrencia " + tipoRegimenConcurrenciaId + " does not exist.");
  }
}
