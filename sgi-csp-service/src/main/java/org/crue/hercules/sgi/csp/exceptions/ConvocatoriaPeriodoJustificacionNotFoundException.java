package org.crue.hercules.sgi.csp.exceptions;

public class ConvocatoriaPeriodoJustificacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConvocatoriaPeriodoJustificacionNotFoundException(Long convocatoriaPeriodoJustificacionId) {
    super("ConvocatoriaPeriodoJustificacion " + convocatoriaPeriodoJustificacionId + " does not exist.");
  }
}
