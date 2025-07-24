package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoAreaConocimientoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessSolicitudException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoAreaConocimientoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoAreaConocimientoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoAreaConocimientoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion
 * {@link SolicitudProyectoAreaConocimiento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SolicitudProyectoAreaConocimientoServiceImpl implements SolicitudProyectoAreaConocimientoService {

  private final SolicitudProyectoAreaConocimientoRepository repository;
  private final SolicitudRepository solicitudRepository;
  private final SolicitudAuthorityHelper solicitudAuthorityHelper;

  @Override
  @Transactional
  public SolicitudProyectoAreaConocimiento create(SolicitudProyectoAreaConocimiento solicitudProyectoAreaConocimiento) {
    log.debug("create(SolicitudProyectoAreaConocimiento solicitudProyectoAreaConocimiento) - start");

    AssertHelper.idIsNull(solicitudProyectoAreaConocimiento.getId(), SolicitudProyectoAreaConocimiento.class);
    AssertHelper.idNotNull(solicitudProyectoAreaConocimiento.getSolicitudProyectoId(), SolicitudProyecto.class);

    SolicitudProyectoAreaConocimiento returnValue = repository.save(solicitudProyectoAreaConocimiento);

    log.debug("create(SolicitudProyectoAreaConocimiento solicitudProyectoAreaConocimiento) - end");
    return returnValue;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, SolicitudProyectoAreaConocimiento.class);

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

    Solicitud solicitud = solicitudRepository.findById(solicitudId)
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));
    if (!(solicitudAuthorityHelper.hasAuthorityViewInvestigador(solicitud)
        || solicitudAuthorityHelper.hasAuthorityViewUnidadGestion(solicitud))) {
      throw new UserNotAuthorizedToAccessSolicitudException();
    }

    Specification<SolicitudProyectoAreaConocimiento> specs = SolicitudProyectoAreaConocimientoSpecifications
        .bySolicitudId(solicitudId).and(SgiRSQLJPASupport.toSpecification(query));
    Page<SolicitudProyectoAreaConocimiento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoId(Long solicitudId, String query, Pageable pageable) - end");
    return returnValue;

  }

}
