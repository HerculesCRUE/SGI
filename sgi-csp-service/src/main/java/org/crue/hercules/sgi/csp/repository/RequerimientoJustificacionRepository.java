package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequerimientoJustificacionRepository
    extends JpaRepository<RequerimientoJustificacion, Long>, JpaSpecificationExecutor<RequerimientoJustificacion> {

  /**
   * Indica si existen {@link RequerimientoJustificacion} asociados al
   * proyectoProyectoSgeId
   * 
   * @param proyectoProyectoSgeId identificador del {@link ProyectoProyectoSge}
   * @return {@code true} si existe al menos un
   *         {@link RequerimientoJustificacion} asociado al
   *         {@link ProyectoProyectoSge}, {@code false} en caso contrario.
   */
  boolean existsByProyectoProyectoSgeId(Long proyectoProyectoSgeId);
}
