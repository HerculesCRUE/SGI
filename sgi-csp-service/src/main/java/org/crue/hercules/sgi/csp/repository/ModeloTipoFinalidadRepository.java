package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoFinalidad;
import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoFinalidad}.
 */
@Repository
public interface ModeloTipoFinalidadRepository
    extends JpaRepository<ModeloTipoFinalidad, Long>, JpaSpecificationExecutor<ModeloTipoFinalidad> {

  /**
   * Recupera {@link ModeloTipoFinalidad} por {@link ModeloEjecucion} y
   * {@link TipoFinalidad}
   * 
   * @param modeloEjecucionId id del ModeloEjecucion
   * @param tipoFinalidadId   id del TipoFinalidad
   * @return ModeloTipoFinalidad
   */
  Optional<ModeloTipoFinalidad> findByModeloEjecucionIdAndTipoFinalidadId(Long modeloEjecucionId, Long tipoFinalidadId);

}