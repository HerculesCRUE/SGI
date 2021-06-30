package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloTipoEnlace}.
 */
public interface ModeloTipoEnlaceService {

  /**
   * Guarda la entidad {@link ModeloTipoEnlace}.
   * 
   * @param modeloTipoEnlace la entidad {@link ModeloTipoEnlace} a guardar.
   * @return la entidad {@link ModeloTipoEnlace} persistida.
   */
  ModeloTipoEnlace create(ModeloTipoEnlace modeloTipoEnlace);

  /**
   * Desactiva el {@link ModeloTipoEnlace}.
   *
   * @param id Id del {@link ModeloTipoEnlace}.
   * @return la entidad {@link ModeloTipoEnlace} persistida.
   */
  ModeloTipoEnlace disable(Long id);

  /**
   * Obtiene una entidad {@link ModeloTipoEnlace} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoEnlace}.
   * @return la entidad {@link ModeloTipoEnlace}.
   */
  ModeloTipoEnlace findById(final Long id);

  /**
   * Obtiene los {@link ModeloTipoEnlace} activos para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoEnlace} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoEnlace> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable);

}
