package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SolicitudProyectoEntidadRepository
    extends JpaRepository<SolicitudProyectoEntidad, Long>, JpaSpecificationExecutor<SolicitudProyectoEntidad> {

  SolicitudProyectoEntidad findBySolicitudProyectoEntidadFinanciadoraAjenaId(
      Long solicitudProyectoEntidadFinanciadoraAjenaId);

  /**
   * Devuelve todas las {@link SolicitudProyectoEntidad} asociados a la
   * {@link Solicitud}
   * 
   * @param solicitudProyectoId Identificador del {@link SolicitudProyecto} y de
   *                            la {@link Solicitud}
   * @return la lista de {@link SolicitudProyectoEntidad} asociados a la
   *         {@link Solicitud}
   */
  List<SolicitudProyectoEntidad> findAllBySolicitudProyectoId(Long solicitudProyectoId);

}
