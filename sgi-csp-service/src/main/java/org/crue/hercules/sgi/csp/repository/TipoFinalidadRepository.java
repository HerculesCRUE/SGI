package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TipoFinalidadRepository
    extends JpaRepository<TipoFinalidad, Long>, JpaSpecificationExecutor<TipoFinalidad> {

  /**
   * Obtiene la entidad {@link TipoFinalidad} activa con el nombre indicado
   *
   * @param nombre el nombre de {@link TipoFinalidad}.
   * @return el {@link TipoFinalidad} con el nombre indicado
   */
  Optional<TipoFinalidad> findByNombreAndActivoIsTrue(String nombre);
}
