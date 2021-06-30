package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoEnlace;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoEnlace}.
 */
@Repository
public interface ModeloTipoEnlaceRepository
    extends JpaRepository<ModeloTipoEnlace, Long>, JpaSpecificationExecutor<ModeloTipoEnlace> {

  /**
   * Busca un {@link ModeloTipoEnlace} por su {@link ModeloEjecucion} y
   * {@link TipoEnlace}.
   * 
   * @param idModeloEjecucion Id del {@link ModeloEjecucion}.
   * @param idTipoEnlace      Id del {@link TipoEnlace}.
   * @return un {@link ModeloTipoEnlace}.
   */
  Optional<ModeloTipoEnlace> findByModeloEjecucionIdAndTipoEnlaceId(Long idModeloEjecucion, Long idTipoEnlace);

}