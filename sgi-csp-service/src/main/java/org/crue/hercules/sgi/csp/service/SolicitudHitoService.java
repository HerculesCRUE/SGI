package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudHito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudHito}.
 */

public interface SolicitudHitoService {

  /**
   * Guarda la entidad {@link SolicitudHito}.
   * 
   * @param solicitudHito la entidad {@link SolicitudHito} a guardar.
   * @return SolicitudHito la entidad {@link SolicitudHito} persistida.
   */
  SolicitudHito create(SolicitudHito solicitudHito);

  /**
   * Actualiza los datos del {@link SolicitudHito}.
   * 
   * @param solicitudHito           {@link SolicitudHito} con los datos
   *                                actualizados.
   * @param isAdministradorOrGestor Indiciador de si el usuario es administrador o
   *                                gestor.
   * @return SolicitudHito {@link SolicitudHito} actualizado.
   */
  SolicitudHito update(final SolicitudHito solicitudHito, Boolean isAdministradorOrGestor);

  /**
   * Obtiene una entidad {@link SolicitudHito} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudHito}.
   * @return SolicitudHito la entidad {@link SolicitudHito}.
   */
  SolicitudHito findById(final Long id);

  /**
   * Elimina el {@link SolicitudHito}.
   *
   * @param id Id del {@link SolicitudHito}.
   */
  void delete(Long id);

  /**
   * Obtiene las {@link SolicitudHito} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudHito} de la {@link Solicitud}
   *         paginadas.
   */
  Page<SolicitudHito> findAllBySolicitud(Long solicitudId, String query, Pageable pageable);

}