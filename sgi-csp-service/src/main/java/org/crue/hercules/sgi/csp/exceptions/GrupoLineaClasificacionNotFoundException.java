package org.crue.hercules.sgi.csp.exceptions;

import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;

public class GrupoLineaClasificacionNotFoundException extends CspNotFoundException {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public GrupoLineaClasificacionNotFoundException(Long grupoLineaClasificacionId) {
    super(grupoLineaClasificacionId, GrupoLineaClasificacion.class);
  }
}
