package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoEvaluacionNotFoundException
 */
public class TipoEvaluacionNotFoundException extends EtiNotFoundException {
  /**
   * Serial version.
   */
  private static final long serialVersionUID = 1L;

  public TipoEvaluacionNotFoundException(Long tipoEvaluacionId) {
    super("TipoEvaluacion " + tipoEvaluacionId + " does not exist.");
  }
}