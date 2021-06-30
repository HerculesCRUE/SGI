package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProgramaNotFoundException
 */
public class ProgramaNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProgramaNotFoundException(Long programaId) {
    super("Programa " + programaId + " does not exist.");
  }
}
