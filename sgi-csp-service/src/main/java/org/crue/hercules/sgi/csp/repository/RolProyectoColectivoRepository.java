package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RolProyectoColectivoRepository
    extends JpaRepository<RolProyectoColectivo, Long>, JpaSpecificationExecutor<RolProyectoColectivo> {

  /**
   * Obtiene el listado de entidades {@link RolProyectoColectivo} asociadas al
   * {@link RolProyecto}
   * 
   * @param rolProyectoId el identificador de {@link RolProyecto}.
   * @return el listado de {@link RolProyectoColectivo}
   */
  List<RolProyectoColectivo> findAllByRolProyectoId(Long rolProyectoId);

}
