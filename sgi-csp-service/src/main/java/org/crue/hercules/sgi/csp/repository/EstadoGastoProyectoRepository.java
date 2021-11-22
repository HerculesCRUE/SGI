package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto;
import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link EstadoGastoProyecto}.
 */
@Repository
public interface EstadoGastoProyectoRepository
    extends JpaRepository<EstadoGastoProyecto, Long>, JpaSpecificationExecutor<EstadoGastoProyecto> {

  /**
   * Obtiene las {@link EstadoGastoProyecto} para una {@link GastoProyecto}.
   *
   * @param idGastoProyecto el id de la {@link GastoProyecto}.
   * @param paging          la información de la paginación.
   * @return la lista de entidades {@link EstadoGastoProyecto} de la
   *         {@link GastoProyecto} paginadas.
   */
  Page<EstadoGastoProyecto> findAllByGastoProyectoId(Long idGastoProyecto, Pageable paging);

}
