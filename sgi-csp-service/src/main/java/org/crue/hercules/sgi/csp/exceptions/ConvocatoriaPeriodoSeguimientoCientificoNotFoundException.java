package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaPeriodoSeguimientoCientificoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaPeriodoSeguimientoCientificoNotFoundException(Long convocatoriaPeriodoSeguimientoCientificoId) {
    super(
        "ConvocatoriaPeriodoSeguimientoCientifico " + convocatoriaPeriodoSeguimientoCientificoId + " does not exist.");
  }
}
