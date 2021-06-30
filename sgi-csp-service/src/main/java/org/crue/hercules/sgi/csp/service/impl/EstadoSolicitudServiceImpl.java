package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.service.EstadoSolicitudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Solicitud}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EstadoSolicitudServiceImpl implements EstadoSolicitudService {

  private final EstadoSolicitudRepository repository;

  public EstadoSolicitudServiceImpl(EstadoSolicitudRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link EstadoSolicitud}.
   * 
   * @param estadoSolicitud la entidad {@link EstadoSolicitud} a guardar.
   * @return estadoSolicitud la entidad {@link EstadoSolicitud} persistida.
   */
  @Override
  @Transactional
  public EstadoSolicitud create(EstadoSolicitud estadoSolicitud) {
    log.debug("create(EstadoSolicitud solicitudModalidad) - start");

    Assert.isNull(estadoSolicitud.getId(), "EstadoSolicitud id tiene que ser null para crear un EstadoSolicitud");

    Assert.notNull(estadoSolicitud.getSolicitudId(), "idSolicitud no puede ser null para crear un EstadoSolicitud");

    EstadoSolicitud returnValue = repository.save(estadoSolicitud);

    log.debug("create(EstadoSolicitud solicitudModalidad) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link EstadoSolicitud} para una {@link Solicitud}.
   *
   * @param idSolicitud el id de la {@link Solicitud}.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link EstadoSolicitud} de la {@link Solicitud}
   *         paginadas.
   */
  @Override
  public Page<EstadoSolicitud> findAllBySolicitud(Long idSolicitud, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, Pageable paging) - start");
    Page<EstadoSolicitud> returnValue = repository.findAllBySolicitudId(idSolicitud, paging);
    log.debug("findAllBySolicitud(Long solicitudId, Pageable paging) - end");
    return returnValue;
  }

}
