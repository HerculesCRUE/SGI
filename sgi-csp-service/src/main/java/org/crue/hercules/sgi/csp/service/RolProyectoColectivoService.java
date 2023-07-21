package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;

/**
 * Service Interface para gestionar {@link RolProyectoColectivo}.
 */

public interface RolProyectoColectivoService {

  /**
   * Guardar un nuevo {@link RolProyectoColectivo}.
   *
   * @param rolProyectoColectivo la entidad {@link RolProyectoColectivo} a
   *                             guardar.
   * @return la entidad {@link RolProyectoColectivo} persistida.
   */
  RolProyectoColectivo create(RolProyectoColectivo rolProyectoColectivo);

  /**
   * Obtiene una lista de colectivos ref filtrados por la entidad
   * {@link RolProyecto}.
   * 
   * @param rolProyectoId Identificador de la entidad {@link RolProyecto}.
   * @return listado de de colectivos ref.
   */
  List<String> findAllColectivos(final Long rolProyectoId);

  /**
   * Obtiene una lista de {@link RolProyectoColectivo} filtrados por la entidad
   * {@link RolProyecto}.
   * 
   * @param rolProyectoId Identificador de la entidad {@link RolProyecto}.
   * @return listado de {@link RolProyectoColectivo} paginado.
   */
  List<RolProyectoColectivo> findAllRolProyectoColectivos(final Long rolProyectoId);

  /**
   * Obtiene un listado de colectivos ref vinculados a {@link RolProyecto} activos
   *
   * @return listado colectivos ref
   */
  List<String> findColectivosActivos();

  /**
   * Elimina una entidad {@link RolProyectoColectivo} por id.
   *
   * @param id el id de la entidad {@link RolProyectoColectivo}.
   */
  void delete(Long id);

}
