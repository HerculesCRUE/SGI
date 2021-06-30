package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoHito}.
 */
@Repository
public interface ModeloTipoHitoRepository
    extends JpaRepository<ModeloTipoHito, Long>, JpaSpecificationExecutor<ModeloTipoHito> {

  /**
   * Recupera {@link ModeloTipoHito} por {@link ModeloEjecucion} y
   * {@link TipoHito}
   * 
   * @param modeloEjecucionId id del ModeloEjecucion
   * @param tipoHitoId        id del TipoHito
   * @return ModeloTipoHito
   */
  Optional<ModeloTipoHito> findByModeloEjecucionIdAndTipoHitoId(Long modeloEjecucionId, Long tipoHitoId);

}