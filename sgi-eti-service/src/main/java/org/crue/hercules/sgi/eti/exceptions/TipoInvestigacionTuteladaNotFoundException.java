package org.crue.hercules.sgi.eti.exceptions;

/**
 * TipoInvestigacionTuteladaNotFoundException
 */
public class TipoInvestigacionTuteladaNotFoundException extends EtiNotFoundException {

  /**
  *
  */
  private static final long serialVersionUID = 1L;

  public TipoInvestigacionTuteladaNotFoundException(Long tipoInvestigacionTuteladaId) {
    super("TipoInvestigacionTutelada " + tipoInvestigacionTuteladaId + " does not exist.");
  }

}