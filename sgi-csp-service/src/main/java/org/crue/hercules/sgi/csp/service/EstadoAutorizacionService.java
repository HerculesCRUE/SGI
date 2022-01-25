package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.AutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.EstadoAutorizacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessAutorizacionException;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.EstadoAutorizacion;
import org.crue.hercules.sgi.csp.repository.AutorizacionRepository;
import org.crue.hercules.sgi.csp.repository.EstadoAutorizacionRepository;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private final AutorizacionRepository autorizacionRepository;

  public EstadoAutorizacionService(EstadoAutorizacionRepository repository,
      AutorizacionRepository autorizacionRepository) {
    this.repository = repository;
    this.autorizacionRepository = autorizacionRepository;
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

}
