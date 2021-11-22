package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.FuenteFinanciacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link FuenteFinanciacion}.
 */
@Repository
public interface FuenteFinanciacionRepository
    extends JpaRepository<FuenteFinanciacion, Long>, JpaSpecificationExecutor<FuenteFinanciacion> {

  /**
   * Obtiene la entidad {@link FuenteFinanciacion} activo con el nombre indicado
   *
   * @param nombre el nombre de {@link FuenteFinanciacion}.
   * @return el {@link FuenteFinanciacion} con el nombre indicado
   */
  Optional<FuenteFinanciacion> findByNombreAndActivoIsTrue(String nombre);
}
