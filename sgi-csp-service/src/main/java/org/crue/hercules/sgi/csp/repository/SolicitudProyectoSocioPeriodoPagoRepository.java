package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioPeriodoPago;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoSocioPeriodoPagoRepository
    extends JpaRepository<SolicitudProyectoSocioPeriodoPago, Long>,
    JpaSpecificationExecutor<SolicitudProyectoSocioPeriodoPago> {

  /**
   * Recupera todos los {@link SolicitudProyectoSocioPeriodoPago} asociados a un
   * {@link SolicitudProyectoSocio}.
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   * @return listado de {@link SolicitudProyectoSocioPeriodoPago}
   */
  List<SolicitudProyectoSocioPeriodoPago> findAllBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

  /**
   * Se eliminan todos los {@link SolicitudProyectoSocioPeriodoPago} asociadosal
   * id de {@link SolicitudProyectoSocio} recibido por parámetro.
   * 
   * @param solicitudProyectoSocioId Identificador de
   *                                 {@link SolicitudProyectoSocio}
   */
  void deleteBySolicitudProyectoSocioId(Long solicitudProyectoSocioId);

}
