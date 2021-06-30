package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;

/**
 * Service Interface para gestionar {@link RolProyectoColectivo}.
 */

public interface RolProyectoColectivoService {

  /**
   * Obtiene una lista de colectivos ref filtrados por la entidad
   * {@link RolProyecto}.
   * 
   * @param rolProyectoId Identificador de la entidad {@link RolProyecto}.
   * @return listado de de colectivos ref.
   */
  List<String> findAllColectivos(final Long rolProyectoId);

}
