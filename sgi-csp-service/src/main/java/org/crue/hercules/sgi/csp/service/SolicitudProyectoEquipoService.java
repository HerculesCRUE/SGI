package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoEquipo}.
 */

public interface SolicitudProyectoEquipoService {

  /**
   * Obtiene una entidad {@link SolicitudProyectoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoEquipo}.
   * @return SolicitudProyectoEquipo la entidad {@link SolicitudProyectoEquipo}.
   */
  SolicitudProyectoEquipo findById(final Long id);

  /**
   * Obtiene las {@link SolicitudProyectoEquipo} para una {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoEquipo} de la
   *         {@link Solicitud} paginadas.
   */
  Page<SolicitudProyectoEquipo> findAllBySolicitud(Long solicitudId, String query, Pageable pageable);

  /**
   * Comprueba si existe la SolicitudProyectoEquipo con el solicitante de la
   * solicitud.
   * 
   * @param solicitudId id de la solicitud.
   * 
   * @return true si existe false si no existe.
   */
  boolean existsSolicitanteInSolicitudProyectoEquipo(Long solicitudId);

  /**
   * Actualiza la lista de {@link SolicitudProyectoEquipo} del
   * {@link SolicitudProyecto}, elimina los que no están en la lista actual pero
   * tienen el mismo solicitudProyectoId , añade los que no tienen id y actualiza
   * los ya existentes.
   * 
   * @param solicitudProyectoId      Identificador de {@link SolicitudProyecto}
   * @param solicitudProyectoEquipos la lista con los cambios para aplicar.
   * @return La lista actualizada de {@link SolicitudProyectoEquipo}.
   */
  List<SolicitudProyectoEquipo> updateSolicitudProyectoEquipo(Long solicitudProyectoId,
      List<SolicitudProyectoEquipo> solicitudProyectoEquipos);
}