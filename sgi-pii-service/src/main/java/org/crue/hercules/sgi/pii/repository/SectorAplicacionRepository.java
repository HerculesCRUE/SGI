package org.crue.hercules.sgi.pii.repository;

import java.util.Optional;

import org.crue.hercules.sgi.pii.model.SectorAplicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SectorAplicacionRepository
    extends ActivableRepository, JpaRepository<SectorAplicacion, Long>, JpaSpecificationExecutor<SectorAplicacion> {

  /**
   * Obtiene la entidad {@link SectorAplicacion} activo con el nombre indicado
   *
   * @param nombre el nombre de {@link SectorAplicacion}.
   * @return el {@link SectorAplicacion} con el nombre indicado
   */
  Optional<SectorAplicacion> findByNombreAndActivoIsTrue(String nombre);
}
