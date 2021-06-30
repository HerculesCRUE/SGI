package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link SolicitudProyectoSocio}.
 */

public interface SolicitudProyectoSocioService {

  /**
   * Guarda la entidad {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio la entidad {@link SolicitudProyectoSocio} a
   *                               guardar.
   * @return SolicitudProyectoSocio la entidad {@link SolicitudProyectoSocio}
   *         persistida.
   */
  SolicitudProyectoSocio create(SolicitudProyectoSocio solicitudProyectoSocio);

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocio {@link SolicitudProyectoSocio} con los datos
   *                               actualizados.
   * 
   * @return SolicitudProyectoSocio {@link SolicitudProyectoSocio} actualizado.
   */
  SolicitudProyectoSocio update(final SolicitudProyectoSocio solicitudProyectoSocio);

  /**
   * Comprueba la existencia del {@link SolicitudProyectoSocio} por id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoSocio}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocio} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoSocio}.
   * @return SolicitudProyectoSocio la entidad {@link SolicitudProyectoSocio}.
   */
  SolicitudProyectoSocio findById(final Long id);

  /**
   * Elimina el {@link SolicitudProyectoSocio}.
   *
   * @param id Id del {@link SolicitudProyectoSocio}.
   */
  void delete(Long id);

  /**
   * Recupera la lista paginada de socios colaborativos de una {@link Solicitud}.
   * 
   * @param idSolicitud Identificador de la {@link Solicitud}.
   * @param query       parámentros de búsqueda.
   * @param paging      parámetros de paginación.
   * @return lista paginada.
   */
  Page<SolicitudProyectoSocio> findAllBySolicitud(Long idSolicitud, String query, Pageable paging);

  /**
   * Indica si {@link SolicitudProyectoSocio} tiene
   * {@link SolicitudProyectoSocioPeriodoJustificacion},
   * {@link SolicitudProyectoSocioPeriodoPago} y/o
   * {@link SolicitudProyectoSocioEquipo} relacionadas.
   *
   * @param id Id de la {@link SolicitudProyectoSocio}.
   * @return True si tiene {@link SolicitudProyectoSocioPeriodoJustificacion},
   *         {@link SolicitudProyectoSocioPeriodoPago} y/o
   *         {@link SolicitudProyectoSocioEquipo} relacionadas. En caso contrario
   *         false
   */
  public Boolean vinculaciones(Long id);

  /**
   * Comprueba si existen datos vinculados a la {@link SolicitudProyecto} de
   * {@link SolicitudProyectoSocio} con el fin de permitir la edición de los campo
   * Proyecto colaborativo
   *
   * @param id Id del {@link SolicitudProyecto}.
   * @return true existen datos vinculados/false no existen datos vinculados.
   */
  Boolean hasSolicitudSocio(Long id);

}