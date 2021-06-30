package org.crue.hercules.sgi.csp.exceptions;

/**
 * RequisitoEquipoNotFoundException
 */
public class RequisitoEquipoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public RequisitoEquipoNotFoundException(Long requisitoEquipoId) {
    super("RequisitoEquipo " + requisitoEquipoId + " does not exist.");
  }

}
