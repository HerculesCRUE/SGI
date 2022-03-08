package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.EstadoAutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link EstadoAutorizacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class EstadoAutorizacionService {
  private final EstadoAutorizacionRepository repository;

  public EstadoAutorizacionService(EstadoAutorizacionRepository repository) {
    this.repository = repository;
  }

  public EstadoAutorizacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final EstadoAutorizacion returnValue = repository.findById(id)
        .orElseThrow(() -> new EstadoAutorizacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link EstadoAutorizacion} para una {@link Autorizacion}.
   *
   * @param autorizacionId el id de la {@link Autorizacion}.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link EstadoAutorizacion} de la
   *         {@link Autorizacion}
   *         paginadas.
   */
  public Page<EstadoAutorizacion> findAllByAutorizacion(Long autorizacionId, Pageable paging) {
    log.debug("findAllByAutorizacion(Long autorizacionId, Pageable paging) - start");

    Page<EstadoAutorizacion> returnValue = repository.findAllByAutorizacionId(autorizacionId, paging);
    log.debug("findAllByAutorizacion(Long autorizacionId, Pageable paging) - end");
    return returnValue;
  }

  public EstadoAutorizacion findFirstEstadoByAutorizacion(Long autorizacionId) {
    log.debug("findFirstEstadoByAutorizacion(Long autorizacionId) - start");

    EstadoAutorizacion returnValue = repository.findFirstByAutorizacionIdOrderByIdAsc(autorizacionId).get(0);
    log.debug("findFirstEstadoByAutorizacion(Long autorizacionId) - end");
    return returnValue;
  }

}
