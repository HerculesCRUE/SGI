package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoSocioEquipo}.
 */

public interface SolicitudProyectoSocioEquipoService {

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocioEquipo}.
   * 
   * @param solicitudProyectoSocioId      Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoEquipoSocios lista con los nuevos
   *                                      {@link SolicitudProyectoSocioEquipo} a
   *                                      guardar.
   * @return SolicitudProyectoSocioEquipo {@link SolicitudProyectoSocioEquipo}
   *         actualizado.
   */
  List<SolicitudProyectoSocioEquipo> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios);

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocioEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoSocioEquipo}.
   * @return SolicitudProyectoSocioEquipo la entidad
   *         {@link SolicitudProyectoSocioEquipo}.
   */
  SolicitudProyectoSocioEquipo findById(final Long id);

  /**
   * Recupera la lista paginada de equipos socio de una
   * {@link SolicitudProyectoSocio}.
   * 
   * @param idSolicitudProyectoSocio Identificador de la
   *                                 {@link SolicitudProyectoSocio}.
   * @param query                    parámentros de búsqueda.
   * @param paging                   parámetros de paginación.
   * @return lista paginada.
   */
  Page<SolicitudProyectoSocioEquipo> findAllBySolicitudProyectoSocio(Long idSolicitudProyectoSocio, String query,
      Pageable paging);

}