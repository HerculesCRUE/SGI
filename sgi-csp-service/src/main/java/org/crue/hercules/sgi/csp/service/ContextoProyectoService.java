package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ContextoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;

/**
 * Service Interface para gestionar {@link ContextoProyecto}.
 */
public interface ContextoProyectoService {

  /**
   * Guardar un nuevo {@link ContextoProyecto}.
   *
   * @param contextoProyecto la entidad {@link ContextoProyecto} a guardar.
   * @return la entidad {@link ContextoProyecto} persistida.
   */
  ContextoProyecto create(ContextoProyecto contextoProyecto);

  /**
   * Actualizar {@link ContextoProyecto}.
   *
   * @param contextoProyectoActualizar la entidad {@link ContextoProyecto} a
   *                                   actualizar.
   * @param idProyecto                 identificador del {@link Proyecto} a
   *                                   actualizar.
   * @return la entidad {@link ContextoProyecto} persistida.
   */
  ContextoProyecto update(ContextoProyecto contextoProyectoActualizar, Long idProyecto);

  /**
   * Obtiene el {@link ContextoProyecto} de la {@link Proyecto}
   * 
   * @param id id del {@link Proyecto}
   * @return la entidad {@link ContextoProyecto}
   */

  ContextoProyecto findByProyecto(Long id);

  /**
   * Indica si existe el {@link ContextoProyecto} de un {@link Proyecto}
   * 
   * @param id identificador de la {@link Proyecto}
   * @return si existe la entidad {@link ContextoProyecto}
   */
  public Boolean existsByProyecto(Long id);

}
