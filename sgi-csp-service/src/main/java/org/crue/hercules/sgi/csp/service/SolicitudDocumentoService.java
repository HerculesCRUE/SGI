package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudDocumento}.
 */

public interface SolicitudDocumentoService {

  /**
   * Guarda la entidad {@link SolicitudDocumento}.
   * 
   * @param solicitudDocumento la entidad {@link SolicitudDocumento} a guardar.
   * @return SolicitudDocumento la entidad {@link SolicitudDocumento} persistida.
   */
  SolicitudDocumento create(SolicitudDocumento solicitudDocumento);

  /**
   * Actualiza los datos del {@link SolicitudDocumento}.
   * 
   * @param solicitudDocumento {@link SolicitudDocumento} con los datos
   *                           actualizados.
   * @return SolicitudDocumento {@link SolicitudDocumento} actualizado.
   */
  SolicitudDocumento update(final SolicitudDocumento solicitudDocumento);

  /**
   * Obtiene una entidad {@link SolicitudDocumento} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudDocumento}.
   * @return SolicitudDocumento la entidad {@link SolicitudDocumento}.
   */
  SolicitudDocumento findById(final Long id);

  /**
   * Elimina el {@link SolicitudDocumento}.
   *
   * @param id Id del {@link SolicitudDocumento}.
   */
  void delete(Long id);

  /**
   * Obtiene las {@link SolicitudDocumento} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudDocumento} de la
   *         {@link Solicitud} paginadas.
   */
  Page<SolicitudDocumento> findAllBySolicitud(Long solicitudId, String query, Pageable pageable);

  /**
   * Guarda la entidad {@link SolicitudDocumento}.
   * 
   * @param solicitudPublicId  el id de la {@link Solicitud}.
   * @param solicitudDocumento la entidad {@link SolicitudDocumento} a guardar.
   * @return la entidad {@link SolicitudDocumento} persistida.
   */
  SolicitudDocumento createByExternalUser(String solicitudPublicId, SolicitudDocumento solicitudDocumento);

  /**
   * Actualiza los datos del {@link SolicitudDocumento}.
   * 
   * @param solicitudPublicId  el id de la {@link Solicitud}.
   * @param solicitudDocumento {@link SolicitudDocumento} con los datos
   *                           actualizados.
   * @return {@link SolicitudDocumento} actualizado.
   */
  SolicitudDocumento updateByExternalUser(String solicitudPublicId, final SolicitudDocumento solicitudDocumento);

  /**
   * Elimina el {@link SolicitudDocumento}.
   *
   * @param solicitudPublicId el id de la {@link Solicitud}.
   * @param id                Id del {@link SolicitudDocumento}.
   */
  void deleteByExternalUser(String solicitudPublicId, Long id);

  /**
   * Obtiene las {@link SolicitudDocumento} para una {@link Solicitud}.
   *
   * @param solicitudPublicId el id de la {@link Solicitud}.
   * @param query             la información del filtro.
   * @param paging            la información de la paginación.
   * @return la lista de entidades {@link SolicitudDocumento} de la
   *         {@link Solicitud} paginadas.
   */
  Page<SolicitudDocumento> findAllBySolicitudPublicId(String solicitudPublicId, String query, Pageable paging);

}
