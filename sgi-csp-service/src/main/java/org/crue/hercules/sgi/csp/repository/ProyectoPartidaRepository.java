package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.enums.TipoPartida;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoPartida}.
 */
@Repository
public interface ProyectoPartidaRepository
    extends JpaRepository<ProyectoPartida, Long>, JpaSpecificationExecutor<ProyectoPartida> {

  boolean existsByProyectoIdAndCodigoAndTipoPartidaAndIdNot(Long proyectoId, String codigo, TipoPartida tipoPartida,
      Long id);

  boolean existsByProyectoIdAndPartidaRefAndTipoPartidaAndIdNot(Long proyectoId, String codigo, TipoPartida tipoPartida,
      Long id);

}
