package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloTipoFinalidad}.
 */

public interface ModeloTipoFinalidadService {

  /**
   * Guarda la entidad {@link ModeloTipoFinalidad}.
   * 
   * @param modeloTipoFinalidad la entidad {@link ModeloTipoFinalidad} a guardar.
   * @return ModeloTipoFinalidad la entidad {@link ModeloTipoFinalidad}
   *         persistida.
   */
  ModeloTipoFinalidad create(ModeloTipoFinalidad modeloTipoFinalidad);

  /**
   * Desactiva el {@link ModeloTipoFinalidad}.
   *
   * @param id Id del {@link ModeloTipoFinalidad}.
   * @return la entidad {@link ModeloTipoFinalidad} persistida.
   */
  ModeloTipoFinalidad disable(Long id);

  /**
   * Obtiene una entidad {@link ModeloTipoFinalidad} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoFinalidad}.
   * @return ModeloTipoFinalidad la entidad {@link ModeloTipoFinalidad}.
   */
  ModeloTipoFinalidad findById(final Long id);

  /**
   * Obtiene los {@link ModeloTipoFinalidad} activos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFinalidad} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoFinalidad> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable);

}
