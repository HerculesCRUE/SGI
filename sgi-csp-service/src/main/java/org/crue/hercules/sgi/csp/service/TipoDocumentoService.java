package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoDocumento}.
 */
public interface TipoDocumentoService {

  /**
   * Guardar un nuevo {@link TipoDocumento}.
   *
   * @param tipoDocumento la entidad {@link TipoDocumento} a guardar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  TipoDocumento create(TipoDocumento tipoDocumento);

  /**
   * Actualizar {@link TipoDocumento}.
   *
   * @param tipoDocumentoActualizar la entidad {@link TipoDocumento} a actualizar.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  TipoDocumento update(TipoDocumento tipoDocumentoActualizar);

  /**
   * Reactiva el {@link TipoDocumento}.
   *
   * @param id Id del {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  TipoDocumento enable(Long id);

  /**
   * Desactiva el {@link TipoDocumento}.
   *
   * @param id Id del {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento} persistida.
   */
  TipoDocumento disable(Long id);

  /**
   * Obtener todas las entidades {@link TipoDocumento} activas paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  Page<TipoDocumento> findAll(String query, Pageable pageable);

  /**
   * Obtener todas las entidades {@link TipoDocumento} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  Page<TipoDocumento> findAllTodos(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoDocumento} por su id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento}.
   */
  TipoDocumento findById(Long id);

  /**
   * Obtener todas las entidades {@link TipoDocumento} asociados a la fase de
   * presentación de solicitudes de la {@link Convocatoria}.
   *
   * @param convocatoriaId identificador de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  Page<TipoDocumento> findAllTipoDocumentosFasePresentacionConvocatoria(Long convocatoriaId, Pageable pageable);

}