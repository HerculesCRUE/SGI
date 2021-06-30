package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.ModeloEjecucion;
import org.crue.hercules.sgi.csp.model.ModeloTipoDocumento;
import org.crue.hercules.sgi.csp.model.ModeloTipoFase;
import org.crue.hercules.sgi.csp.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ModeloTipoDocumento}.
 */
@Repository
public interface ModeloTipoDocumentoRepository
    extends JpaRepository<ModeloTipoDocumento, Long>, JpaSpecificationExecutor<ModeloTipoDocumento> {

  /**
   * Busca {@link ModeloTipoDocumento} por {@link ModeloEjecucion} y
   * {@link TipoDocumento}.
   * 
   * @param idModeloEjecucion Id del {@link ModeloEjecucion}.
   * @param idTipoDocumento   Id del {@link TipoDocumento}.
   * @return lista {@link ModeloTipoDocumento} con el modelo de ejecución.
   */
  List<ModeloTipoDocumento> findByModeloEjecucionIdAndTipoDocumentoId(Long idModeloEjecucion, Long idTipoDocumento);

  /**
   * Busca {@link ModeloTipoDocumento} por {@link ModeloEjecucion} y
   * {@link TipoDocumento}.
   * 
   * @param modeloEjecucionId Id del {@link ModeloEjecucion}.
   * @param modeloTipoFaseId  Id del {@link ModeloTipoFase}.
   * @param tipoDocumentoId   Id del {@link TipoDocumento}.
   * @return un {@link ModeloTipoDocumento}.
   */
  Optional<ModeloTipoDocumento> findByModeloEjecucionIdAndModeloTipoFaseIdAndTipoDocumentoId(Long modeloEjecucionId,
      Long modeloTipoFaseId, Long tipoDocumentoId);
}