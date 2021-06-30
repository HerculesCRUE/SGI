package org.crue.hercules.sgi.csp.exceptions;

/**
 * ProyectoDocumentoNotFoundException
 */
public class ProyectoDocumentoNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public ProyectoDocumentoNotFoundException(Long proyectoDocumentoId) {
    super("Proyecto documento " + proyectoDocumentoId + " does not exist.");
  }

}
