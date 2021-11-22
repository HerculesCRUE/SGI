package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Informe}.
 */
@Repository
public interface InformeRepository extends JpaRepository<Informe, Long>, JpaSpecificationExecutor<Informe> {

  /**
   * Devuelve el último informe asociado a la memoria ordenado por la versión desc
   * 
   * @param idMemoria identificador de la {@link Memoria}
   * @return el {@link Informe}
   */
  Optional<Informe> findFirstByMemoriaIdOrderByVersionDesc(Long idMemoria);

  Page<Informe> findByMemoriaId(Long idMemoria, Pageable pageable);

  /**
   * Devuelve el {@link Informe} filtrado por la {@link Memoria} y su tipo de
   * evaluación
   * 
   * @param idMemoria        identificador de la {@link Memoria}
   * @param idTipoEvaluacion identificador del {@link TipoEvaluacion}
   * @return el {@link Informe}
   */
  Optional<Informe> findFirstByMemoriaIdAndTipoEvaluacionIdOrderByVersionDesc(Long idMemoria, Long idTipoEvaluacion);
}