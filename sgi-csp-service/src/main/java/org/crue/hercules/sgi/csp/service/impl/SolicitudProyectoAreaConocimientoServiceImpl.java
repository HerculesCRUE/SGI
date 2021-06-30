package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoAreaConocimientoNotFoundException;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoAreaConocimientoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoAreaConocimientoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoAreaConocimientoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.modelmapper.internal.util.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link SolicitudProyectoAreaConocimiento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoAreaConocimientoServiceImpl implements SolicitudProyectoAreaConocimientoService {

  private final SolicitudProyectoAreaConocimientoRepository repository;

  public SolicitudProyectoAreaConocimientoServiceImpl(SolicitudProyectoAreaConocimientoRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public SolicitudProyectoAreaConocimiento create(SolicitudProyectoAreaConocimiento solicitudProyectoAreaConocimiento) {
    log.debug("create(SolicitudProyectoAreaConocimiento solicitudProyectoAreaConocimiento) - start");

    Assert.isNull(solicitudProyectoAreaConocimiento.getId(),
        "Id tiene que ser null para crear SolicitudProyectoAreaConocimiento");
    Assert.notNull(solicitudProyectoAreaConocimiento.getSolicitudProyectoId(),
        "Id SolicitudProyecto no puede ser null para crear SolicitudProyectoAreaConocimiento");

    SolicitudProyectoAreaConocimiento returnValue = repository.save(solicitudProyectoAreaConocimiento);

    log.debug("create(SolicitudProyectoAreaConocimiento solicitudProyectoAreaConocimiento) - end");
    return returnValue;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "SolicitudProyectoAreaConocimiento id no puede ser null para desactivar un SolicitudProyectoAreaConocimiento");

    if (!repository.existsById(id)) {
      throw new SolicitudProyectoAreaConocimientoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  @Override
  public Page<SolicitudProyectoAreaConocimiento> findAllBySolicitudProyectoId(Long solicitudId, String query,
      Pageable paging) {
    log.debug("findAllBySolicitudProyectoId(Long solicitudId, String query, Pageable pageable) - start");
    Specification<SolicitudProyectoAreaConocimiento> specs = SolicitudProyectoAreaConocimientoSpecifications
        .bySolicitudId(solicitudId).and(SgiRSQLJPASupport.toSpecification(query));
    Page<SolicitudProyectoAreaConocimiento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoId(Long solicitudId, String query, Pageable pageable) - end");
    return returnValue;

  }

}
