package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudModalidadRepository
    extends JpaRepository<SolicitudModalidad, Long>, JpaSpecificationExecutor<SolicitudModalidad> {

  /**
   * Obtiene las {@link SolicitudModalidad} asociadas a una {@link Solicitud}
   * 
   * @param solicitudId Identificador de la {@link Solicitud}
   * @return Listado de solicitudes modalidad
   */
  List<SolicitudModalidad> findAllBySolicitudId(Long solicitudId);

}
