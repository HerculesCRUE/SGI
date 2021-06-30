package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.DocumentoRequeridoSolicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link DocumentoRequeridoSolicitud}.
 */

public interface DocumentoRequeridoSolicitudService {

  /**
   * Guarda la entidad {@link DocumentoRequeridoSolicitud}.
   * 
   * @param documentoRequeridoSolicitud la entidad
   *                                    {@link DocumentoRequeridoSolicitud} a
   *                                    guardar.
   * @return DocumentoRequeridoSolicitud la entidad
   *         {@link DocumentoRequeridoSolicitud} persistida.
   */
  DocumentoRequeridoSolicitud create(DocumentoRequeridoSolicitud documentoRequeridoSolicitud);

  /**
   * Actualiza los datos del {@link DocumentoRequeridoSolicitud}
   * 
   * @param documentoRequeridoSolicitud {@link DocumentoRequeridoSolicitud} a
   *                                    actualizar.
   * @return DocumentoRequeridoSolicitud {@link DocumentoRequeridoSolicitud}
   *         actualizado.
   */
  DocumentoRequeridoSolicitud update(final DocumentoRequeridoSolicitud documentoRequeridoSolicitud);

  /**
   * Elimina la {@link DocumentoRequeridoSolicitud}.
   *
   * @param id Id del {@link DocumentoRequeridoSolicitud}.
   */
  void delete(Long id);

  /**
   * Obtiene una entidad {@link DocumentoRequeridoSolicitud} por el id.
   * 
   * @param id Identificador de {@link DocumentoRequeridoSolicitud}.
   * @return DocumentoRequeridoSolicitud la entidad
   *         {@link DocumentoRequeridoSolicitud}.
   */
  DocumentoRequeridoSolicitud findById(final Long id);

  /**
   * Obtiene las {@link DocumentoRequeridoSolicitud} para una {@link Convocatoria
   * }.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link DocumentoRequeridoSolicitud} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<DocumentoRequeridoSolicitud> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable);

}
