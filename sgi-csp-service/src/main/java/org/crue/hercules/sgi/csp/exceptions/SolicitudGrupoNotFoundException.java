package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.SolicitudGrupo;

public class SolicitudGrupoNotFoundException extends CspNotFoundException {

  /**
   * SolicitudGrupoNotFoundException
   */
  private static final long serialVersionUID = 1L;

  public SolicitudGrupoNotFoundException(Long id) {
    super(id, SolicitudGrupo.class);
  }

}
