package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloEjecucion}.
 */
@Repository
public interface ModeloEjecucionRepository
    extends JpaRepository<ModeloEjecucion, Long>, JpaSpecificationExecutor<ModeloEjecucion> {

  /**
   * Busca un {@link ModeloEjecucion} activo por nombre.
   * 
   * @param nombre Nombre del {@link ModeloEjecucion}.
   * @return un {@link ModeloEjecucion} si tiene el nombre buscado.
   */
  Optional<ModeloEjecucion> findByNombreAndActivoIsTrue(String nombre);

}