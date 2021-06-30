package org.crue.hercules.sgi.csp.exceptions;

public class ConfiguracionSolicitudNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ConfiguracionSolicitudNotFoundException(Long convocatoriaId) {
    super("ConfiguracionSolicitud with Convocatoria Id " + convocatoriaId + " does not exist.");
  }
}
