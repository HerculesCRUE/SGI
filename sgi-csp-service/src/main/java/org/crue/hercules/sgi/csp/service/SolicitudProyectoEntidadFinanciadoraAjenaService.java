package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar
 * {@link SolicitudProyectoEntidadFinanciadoraAjena}.
 */
public interface SolicitudProyectoEntidadFinanciadoraAjenaService {

  /**
   * Guardar un nuevo {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *
   * @param solicitudProyectoEntidadFinanciadoraAjena la entidad
   *                                                  {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                  a guardar.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *         persistida.
   */
  SolicitudProyectoEntidadFinanciadoraAjena create(
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjena);

  /**
   * Actualizar {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *
   * @param solicitudProyectoEntidadFinanciadoraAjenaActualizar la entidad
   *                                                            {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *                                                            a actualizar.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *         persistida.
   */
  SolicitudProyectoEntidadFinanciadoraAjena update(
      SolicitudProyectoEntidadFinanciadoraAjena solicitudProyectoEntidadFinanciadoraAjenaActualizar);

  /**
   * Elimina el {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   *
   * @param id Id del {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link SolicitudProyectoEntidadFinanciadoraAjena} por su id.
   *
   * @param id el id de la entidad
   *           {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return la entidad {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   */
  SolicitudProyectoEntidadFinanciadoraAjena findById(Long id);

  /**
   * Obtiene las {@link SolicitudProyectoEntidadFinanciadoraAjena} para una
   * {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades
   *         {@link SolicitudProyectoEntidadFinanciadoraAjena} de la
   *         {@link Solicitud} paginadas.
   */
  Page<SolicitudProyectoEntidadFinanciadoraAjena> findAllBySolicitud(Long solicitudId, String query, Pageable pageable);
}