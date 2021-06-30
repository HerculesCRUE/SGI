package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoActividadNotFoundException
 */
public class TipoActividadNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoActividadNotFoundException(Long tipoActividadId) {
    super("TipoActividad " + tipoActividadId + " does not exist.");
  }

}