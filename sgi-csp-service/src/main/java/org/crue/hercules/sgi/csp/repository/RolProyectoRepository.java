package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RolProyectoRepository extends JpaRepository<RolProyecto, Long>, JpaSpecificationExecutor<RolProyecto> {

  /**
   * Obtiene la entidad {@link RolProyecto} activa con la abreviatura indicada
   *
   * @param abreviatura la abreviatura de {@link RolProyecto}.
   * @return el {@link RolProyecto} activo con la abreviatura indicada
   */
  Optional<RolProyecto> findByAbreviaturaAndActivoIsTrue(String abreviatura);

  /**
   * Obtiene la entidad {@link RolProyecto} activa con el nombre indicado
   *
   * @param nombre el nombre de {@link RolProyecto}.
   * @return el {@link RolProyecto} activo con el nombre indicado
   */
  Optional<RolProyecto> findByNombreAndActivoIsTrue(String nombre);
}
