package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoSocioPeriodoJustificacionRepository
    extends JpaRepository<SolicitudProyectoSocioPeriodoJustificacion, Long>,
    JpaSpecificationExecutor<SolicitudProyectoSocioPeriodoJustificacion> {

  /**
   * Recupera el listado de solicitud proyecto periodo justificación asociados a
   * una solicitud proyecto socio id.
   * 
   * @param idSolicitudProyectoSocio Identificador de un
   *                                 {@link SolicitudProyectoSocio}
   * @return listado de {@link SolicitudProyectoSocioPeriodoJustificacion}
   */
  List<SolicitudProyectoSocioPeriodoJustificacion> findAllBySolicitudProyectoSocioId(Long idSolicitudProyectoSocio);

  /**
   * Se eliminan todos los {@link SolicitudProyectoSocioPeriodoJustificacion}
   * asociadosal id de {@link SolicitudProyectoSocio} recibido por parámetro.
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   */
  void deleteBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

}
