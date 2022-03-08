package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.NotificacionCVNEntidadFinanciadora;
import org.crue.hercules.sgi.csp.repository.NotificacionCVNEntidadFinanciadoraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link NotificacionCVNEntidadFinanciadora}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class NotificacionCVNEntidadFinanciadoraService {

  private final NotificacionCVNEntidadFinanciadoraRepository repository;

  public NotificacionCVNEntidadFinanciadoraService(NotificacionCVNEntidadFinanciadoraRepository repository) {
    this.repository = repository;
  }

  /**
   * * Obtener todas las entidades {@link NotificacionCVNEntidadFinanciadora}
   * paginadas pertenecientes a una Notificacion Proyecto externo
   * 
   * @param notificacionProyectoExternoCvnId id de la Notificacion Proyecto
   *                                         externo
   * @param paging                           paginacion
   * @return lista de las entidades fionanciadoras
   */
  public Page<NotificacionCVNEntidadFinanciadora> findAllByNotificacionProyectoExternoCvnId(
      Long notificacionProyectoExternoCvnId, Pageable paging) {
    log.debug("findAll(String query, Pageable pageable) - start");

    Page<NotificacionCVNEntidadFinanciadora> returnValue = repository
        .findByNotificacionProyectoExternoCvnId(notificacionProyectoExternoCvnId, paging);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }
}
