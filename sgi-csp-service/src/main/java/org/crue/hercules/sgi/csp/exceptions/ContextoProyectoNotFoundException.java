package org.crue.hercules.sgi.csp.exceptions;

/**
 * ContextoProyectoNotFoundException
 */
public class ContextoProyectoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ContextoProyectoNotFoundException(Long contextoProyectoId) {
    super("ContextoProyecto " + contextoProyectoId + " does not exist.");
  }

}
