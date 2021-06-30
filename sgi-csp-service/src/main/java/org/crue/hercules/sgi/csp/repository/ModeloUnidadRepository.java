package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloUnidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloUnidad}.
 */
@Repository
public interface ModeloUnidadRepository
    extends JpaRepository<ModeloUnidad, Long>, JpaSpecificationExecutor<ModeloUnidad> {

  /**
   * Busca un {@link ModeloUnidad} por su {@link ModeloEjecucion} y
   * unidadGestionRef.
   * 
   * @param idModeloEjecucion Id del {@link ModeloEjecucion}.
   * @param unidadGestionRef  Id de la unidadGestion.
   * @return un {@link ModeloUnidad}.
   */
  Optional<ModeloUnidad> findByModeloEjecucionIdAndUnidadGestionRef(Long idModeloEjecucion, String unidadGestionRef);

}