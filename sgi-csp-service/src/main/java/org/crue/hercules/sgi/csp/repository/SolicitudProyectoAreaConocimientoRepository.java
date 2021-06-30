package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository para {@link SolicitudProyectoAreaConocimiento}.
 */

public interface SolicitudProyectoAreaConocimientoRepository
    extends JpaRepository<SolicitudProyectoAreaConocimiento, Long>,
    JpaSpecificationExecutor<SolicitudProyectoAreaConocimiento> {

  /**
   * Obtiene las {@link SolicitudProyectoAreaConocimiento} asociadas a una
   * {@link SolicitudProyecto}
   * 
   * @param solicitudProyectoId Identificador de la {@link SolicitudProyecto}
   * @return Listado de areas de conocimiento
   */
  List<SolicitudProyectoAreaConocimiento> findAllBySolicitudProyectoId(Long solicitudProyectoId);
}
