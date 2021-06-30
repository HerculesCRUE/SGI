package org.crue.hercules.sgi.pii.repository;

import java.util.Optional;

import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link TipoProteccion}.
 */
@Repository
public interface TipoProteccionRepository
    extends ActivableRepository, JpaRepository<TipoProteccion, Long>, JpaSpecificationExecutor<TipoProteccion> {

  /**
   * Obtiene la entidad {@link TipoProteccion} activo con el nombre indicado
   *
   * @param nombre el nombre de {@link TipoProteccion}.
   * @return el {@link TipoProteccion} con el nombre indicado
   */
  Optional<TipoProteccion> findByNombreAndActivoIsTrue(String nombre);
}
