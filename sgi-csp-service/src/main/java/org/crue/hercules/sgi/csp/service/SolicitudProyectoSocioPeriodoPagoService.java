package org.crue.hercules.sgi.csp.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoSocioPeriodoPago}.
 */

public interface SolicitudProyectoSocioPeriodoPagoService {

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocioPeriodoPago} por id.
   * 
   * @param id Identificador de la entidad
   *           {@link SolicitudProyectoSocioPeriodoPago}.
   * @return SolicitudProyectoSocioPeriodoPago la entidad
   *         {@link SolicitudProyectoSocioPeriodoPago}.
   */
  SolicitudProyectoSocioPeriodoPago findById(final Long id);

  /**
   * Recupera la lista paginada de socios colaborativos de una
   * {@link SolicitudProyectoSocio}.
   * 
   * @param idSolicitudProyectoProyectoSocio Identificador de la
   *                                         {@link SolicitudProyectoSocio}.
   * @param query                            parámentros de búsqueda.
   * @param paging                           parámetros de paginación.
   * @return lista paginada.
   */
  Page<SolicitudProyectoSocioPeriodoPago> findAllBySolicitudProyectoSocio(Long idSolicitudProyectoProyectoSocio,
      String query, Pageable paging);

  /**
   * Actualiza el listado de {@link SolicitudProyectoSocioPeriodoPago} de la
   * {@link SolicitudProyectoSocio} con el listado solicitudPeriodoPagos
   * añadiendo, editando o eliminando los elementos segun proceda.
   *
   * @param solicitudProyectoSocioId Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudPeriodoPagos    lista con los nuevos
   *                                 {@link SolicitudProyectoSocioPeriodoPago} a
   *                                 guardar.
   * @return la entidad {@link SolicitudProyectoSocioPeriodoPago} persistida.
   */
  List<SolicitudProyectoSocioPeriodoPago> update(Long solicitudProyectoSocioId,
      @Valid List<SolicitudProyectoSocioPeriodoPago> solicitudPeriodoPagos);

}