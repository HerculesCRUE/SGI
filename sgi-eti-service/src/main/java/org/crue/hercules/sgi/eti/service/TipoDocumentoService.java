package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link TipoDocumento}.
 */
public interface TipoDocumentoService {

  /**
   * Obtener todas las entidades {@link TipoDocumento} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoDocumento} paginadas y/o filtradas.
   */
  Page<TipoDocumento> findAll(String query, Pageable pageable);

  /**
   * Obtiene {@link TipoDocumento} por id.
   *
   * @param id el id de la entidad {@link TipoDocumento}.
   * @return la entidad {@link TipoDocumento}.
   */
  TipoDocumento findById(Long id);

  List<TipoDocumento> findByFormularioId(Long formularioId);

}