package org.crue.hercules.sgi.csp.exceptions;

public class LineaInvestigacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public LineaInvestigacionNotFoundException(Long lineaInvestigacionId) {
    super("LineaInvestigacion " + lineaInvestigacionId + " does not exist.");
  }
}
