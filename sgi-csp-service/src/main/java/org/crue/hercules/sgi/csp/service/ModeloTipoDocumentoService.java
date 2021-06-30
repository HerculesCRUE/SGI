package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ModeloTipoDocumento}.
 */
public interface ModeloTipoDocumentoService {

  /**
   * Guarda la entidad {@link ModeloTipoDocumento}.
   * 
   * @param modeloTipoDocumento la entidad {@link ModeloTipoDocumento} a guardar.
   * @return la entidad {@link ModeloTipoDocumento} persistida.
   */
  ModeloTipoDocumento create(ModeloTipoDocumento modeloTipoDocumento);

  /**
   * Desactiva el {@link ModeloTipoDocumento}.
   *
   * @param id Id del {@link ModeloTipoDocumento}.
   * @return la entidad {@link ModeloTipoDocumento} persistida.
   */
  ModeloTipoDocumento disable(Long id);

  /**
   * Obtiene una entidad {@link ModeloTipoDocumento} por id.
   * 
   * @param id Identificador de la entidad {@link ModeloTipoDocumento}.
   * @return la entidad {@link ModeloTipoDocumento}.
   */
  ModeloTipoDocumento findById(final Long id);

  /**
   * Obtiene los {@link ModeloTipoDocumento} activos para un
   * {@link ModeloEjecucion}.
   *
   * @param idModeloEjecucion el id de la entidad {@link ModeloEjecucion}.
   * @param query             la información del filtro.
   * @param pageable          la información de la paginación.
   * @return la lista de entidades {@link ModeloTipoDocumento} del
   *         {@link ModeloEjecucion} paginadas.
   */
  Page<ModeloTipoDocumento> findAllByModeloEjecucion(Long idModeloEjecucion, String query, Pageable pageable);

}
