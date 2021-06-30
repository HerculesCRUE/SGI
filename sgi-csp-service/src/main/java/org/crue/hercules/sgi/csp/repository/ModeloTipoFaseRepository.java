package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoFase}.
 */
@Repository
public interface ModeloTipoFaseRepository
    extends JpaRepository<ModeloTipoFase, Long>, JpaSpecificationExecutor<ModeloTipoFase> {

  /**
   * Busca un {@link ModeloTipoFase} por su modelo de ejecución y su tipoFase
   * 
   * @param modeloEjecucionId Id del ModeloEjecución del {@link ModeloTipoFase}.
   * @param tipoFaseId        Id del TipoFase del {@link ModeloTipoFase}
   * @return un {@link ModeloTipoFase} si tiene el ModeloEjecución buscado.
   */
  Optional<ModeloTipoFase> findByModeloEjecucionIdAndTipoFaseId(Long modeloEjecucionId, Long tipoFaseId);

}