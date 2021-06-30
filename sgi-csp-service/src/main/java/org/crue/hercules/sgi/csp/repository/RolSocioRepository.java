package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.RolSocio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RolSocioRepository extends JpaRepository<RolSocio, Long>, JpaSpecificationExecutor<RolSocio> {

  /**
   * Obtiene la entidad {@link RolSocio} activa con la abreviatura indicada
   *
   * @param abreviatura la abreviatura de {@link RolSocio}.
   * @return el {@link RolSocio} activo con la abreviatura indicada
   */
  Optional<RolSocio> findByAbreviaturaAndActivoIsTrue(String abreviatura);

  /**
   * Obtiene la entidad {@link RolSocio} activa con el nombre indicado
   *
   * @param nombre el nombre de {@link RolSocio}.
   * @return el {@link RolSocio} activo con el nombre indicado
   */
  Optional<RolSocio> findByNombreAndActivoIsTrue(String nombre);
}
