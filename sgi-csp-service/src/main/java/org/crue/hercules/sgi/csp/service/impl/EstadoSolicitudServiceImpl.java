package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.model.EstadoSolicitud;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudModalidad;
import org.crue.hercules.sgi.csp.repository.EstadoSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.EstadoSolicitudSpecifications;
import org.crue.hercules.sgi.csp.service.EstadoSolicitudService;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
  private final SolicitudAuthorityHelper authorityHelper;

  public EstadoSolicitudServiceImpl(EstadoSolicitudRepository repository, SolicitudAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.authorityHelper = authorityHelper;
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
   * @param solicitudId el id de la {@link Solicitud}.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link EstadoSolicitud} de la {@link Solicitud}
   *         paginadas.
   */
  @Override
  public Page<EstadoSolicitud> findAllBySolicitud(Long solicitudId, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, Pageable paging) - start");

    authorityHelper.checkUserHasAuthorityViewSolicitud(solicitudId);

    Page<EstadoSolicitud> returnValue = repository.findAllBySolicitudId(solicitudId, paging);
    log.debug("findAllBySolicitud(Long solicitudId, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudModalidad} para una {@link Solicitud}.
   *
   * @param solicitudPublicId el id de la {@link Solicitud}.
   * @param paging            la información de la paginación.
   * @return la lista de entidades {@link SolicitudModalidad} de la
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<EstadoSolicitud> findAllBySolicitudPublicId(String solicitudPublicId, Pageable paging) {
    log.debug("findAllBySolicitudPublicId(String solicitudPublicId, Pageable paging) - start");
    Long solicitudId = authorityHelper.getSolicitudIdByPublicId(solicitudPublicId);
    Specification<EstadoSolicitud> specs = EstadoSolicitudSpecifications.bySolicitudId(solicitudId);

    Page<EstadoSolicitud> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudPublicId(String solicitudPublicId, Pageable paging) - end");
    return returnValue;
  }

}
