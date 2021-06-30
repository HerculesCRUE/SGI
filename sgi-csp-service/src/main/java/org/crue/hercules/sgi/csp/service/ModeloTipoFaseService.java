package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.ModeloTipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloTipoFase}.
 */

public interface ModeloTipoFaseService {

  /**
   * Guardar {@link ModeloTipoFase}.
   *
   * @param modeloTipoFase la entidad {@link ModeloTipoFase} a guardar.
   * @return la entidad {@link ModeloTipoFase} persistida.
   */
  ModeloTipoFase create(ModeloTipoFase modeloTipoFase);

  /**
   * Actualizar {@link ModeloTipoFase}.
   *
   * @param modeloTipoFase la entidad {@link ModeloTipoFase} a actualizar.
   * @return la entidad {@link ModeloTipoFase} persistida.
   */
  ModeloTipoFase update(ModeloTipoFase modeloTipoFase);

  /**
   * Obtiene {@link ModeloTipoFase} por id.
   *
   * @param id el id de la entidad {@link ModeloTipoFase}.
   * @return la entidad {@link ModeloTipoFase}.
   */
  ModeloTipoFase findById(Long id);

  /**
   * Desactiva el {@link ModeloTipoFase} por id.
   *
   * @param id el id de la entidad {@link ModeloTipoFase}.
   * @return la entidad {@link ModeloTipoFase} persistida.
   */
  ModeloTipoFase disable(Long id) throws ModeloTipoFaseNotFoundException;

  /**
   * Obtiene los {@link ModeloTipoFase} para un {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoFase> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable);

  /**
   * Obtiene los {@link ModeloTipoFase} activos para convocatorias para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoFase> findAllByModeloEjecucionActivosConvocatoria(Long idModeloEjecucion, String query,
      Pageable pageable);

  /**
   * Obtiene los {@link ModeloTipoFase} activos para proyectos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoFase} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoFase> findAllByModeloEjecucionActivosProyecto(Long idModeloEjecucion, String query, Pageable pageable);

}
