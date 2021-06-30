package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link SolicitudProyectoClasificacion}.
 */
@Repository
public interface SolicitudProyectoClasificacionRepository extends JpaRepository<SolicitudProyectoClasificacion, Long>,
    JpaSpecificationExecutor<SolicitudProyectoClasificacion> {

  /**
   * Obtiene las {@link SolicitudProyectoClasificacion} asociadas a una
   * {@link SolicitudProyecto}
   * 
   * @param solicitudProyectoId Identificador de la {@link SolicitudProyecto}
   * @return Listado de clasificaciones
   */
  List<SolicitudProyectoClasificacion> findAllBySolicitudProyectoId(Long solicitudProyectoId);
}
