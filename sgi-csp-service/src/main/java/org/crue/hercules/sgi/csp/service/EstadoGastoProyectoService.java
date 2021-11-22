package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.EstadoGastoProyecto;
import org.crue.hercules.sgi.csp.model.GastoProyecto;
import org.crue.hercules.sgi.csp.repository.EstadoGastoProyectoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EstadoGastoProyectoService {

  private final EstadoGastoProyectoRepository repository;

  public EstadoGastoProyectoService(EstadoGastoProyectoRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene las {@link EstadoGastoProyecto} para una {@link GastoProyecto}.
   *
   * @param idGastoProyecto el id de la {@link GastoProyecto}.
   * @param paging          la información de la paginación.
   * @return la lista de entidades {@link EstadoGastoProyecto} de la
   *         {@link GastoProyecto} paginadas.
   */
  public Page<EstadoGastoProyecto> findAllByGastoProyecto(Long idGastoProyecto, Pageable paging) {
    log.debug("findAllByGastoProyecto(Long idGastoProyecto, Pageable paging) - start");
    Page<EstadoGastoProyecto> returnValue = repository.findAllByGastoProyectoId(idGastoProyecto, paging);
    log.debug("findAllByGastoProyecto(Long idGastoProyecto, Pageable paging) - end");
    return returnValue;
  }

}
