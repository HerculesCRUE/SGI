package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.crue.hercules.sgi.csp.model.Proyecto;

/**
 * Service Interface para gestionar {@link EstadoProyecto}.
 */
public interface EstadoProyectoService {

  /**
   * Guarda la entidad {@link EstadoProyecto}.
   * 
   * @param estadoProyecto la entidad {@link EstadoProyecto} a guardar.
   * @return ProyectoModalidad la entidad {@link EstadoProyecto} persistida.
   */
  EstadoProyecto create(EstadoProyecto estadoProyecto);

  /**
   * Obtiene las {@link EstadoProyecto} para una {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link EstadoProyecto} de la {@link Proyecto}
   *         paginadas.
   */
  Page<EstadoProyecto> findAllByProyecto(Long idProyecto, Pageable pageable);

}
