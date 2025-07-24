package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloTipoHito}.
 */
public interface ModeloTipoHitoService {

  /**
   * Guarda la entidad {@link ModeloTipoHito}.
   * 
   * @param modeloTipoHito la entidad {@link ModeloTipoHito} a guardar.
   * @return ModeloTipoHito la entidad {@link ModeloTipoHito} persistida.
   */
  ModeloTipoHito create(ModeloTipoHito modeloTipoHito);

  /**
   * Actualizar {@link ModeloTipoHito}.
   *
   * @param modeloTipoHito la entidad {@link ModeloTipoHito} a actualizar.
   * @return la entidad {@link ModeloTipoHito} persistida.
   */
  ModeloTipoHito update(ModeloTipoHito modeloTipoHito);

  /**
   * Desactiva el {@link ModeloTipoHito}.
   *
   * @param id Id del {@link ModeloTipoHito}.
   * @return la entidad {@link ModeloTipoHito} persistida.
   */
  ModeloTipoHito disable(Long id);

  /**
   * Obtiene una entidad {@link ModeloTipoHito} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoHito}.
   * @return ModeloTipoHito la entidad {@link ModeloTipoHito}.
   */
  ModeloTipoHito findById(final Long id);

  /**
   * Obtiene los {@link ModeloTipoHito} para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link TipoHito} del {@link ModeloEjecucion}
   *         paginadas.
   */
  Page<ModeloTipoHito> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable);

}
