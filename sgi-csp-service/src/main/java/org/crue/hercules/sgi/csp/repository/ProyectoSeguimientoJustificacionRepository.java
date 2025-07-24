package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSeguimientoJustificacionRepository
    extends JpaRepository<ProyectoSeguimientoJustificacion, Long>,
    JpaSpecificationExecutor<ProyectoSeguimientoJustificacion> {

  /**
   * Indica si existen {@link ProyectoSeguimientoJustificacion} asociados al
   * proyectoProyectoSgeId
   * 
   * @param proyectoProyectoSgeId identificador del {@link ProyectoProyectoSge}
   * @return {@code true} si existe al menos un
   *         {@link ProyectoSeguimientoJustificacion} asociado al
   *         {@link ProyectoProyectoSge}, {@code false} en caso contrario.
   */
  boolean existsByProyectoProyectoSgeId(Long proyectoProyectoSgeId);

}
