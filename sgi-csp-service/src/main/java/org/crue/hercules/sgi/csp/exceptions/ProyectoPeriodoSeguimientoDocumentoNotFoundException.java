package org.crue.hercules.sgi.csp.exceptions;

public class ProyectoPeriodoSeguimientoDocumentoNotFoundException extends CspNotFoundException {

  /**
   * ProyectoPeriodoSeguimientoDocumentoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public ProyectoPeriodoSeguimientoDocumentoNotFoundException(Long proyectoPeriodoSeguimientoDocumentoId) {
    super("ProyectoPeriodoSeguimientoDocumento " + proyectoPeriodoSeguimientoDocumentoId + " does not exist.");
  }
}
