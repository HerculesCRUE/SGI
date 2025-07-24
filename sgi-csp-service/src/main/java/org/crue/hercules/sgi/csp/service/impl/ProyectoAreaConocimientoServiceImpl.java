package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ProyectoAreaConocimientoNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoAreaConocimiento;
import org.crue.hercules.sgi.csp.repository.ProyectoAreaConocimientoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoAreaConocimientoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoAreaConocimientoService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ProyectoAreaConocimiento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoAreaConocimientoServiceImpl implements ProyectoAreaConocimientoService {

  private final ProyectoAreaConocimientoRepository repository;

  public ProyectoAreaConocimientoServiceImpl(ProyectoAreaConocimientoRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public ProyectoAreaConocimiento create(ProyectoAreaConocimiento proyectoAreaConocimiento) {
    log.debug("create(ProyectoAreaConocimiento proyectoAreaConocimiento) - start");

    AssertHelper.idIsNull(proyectoAreaConocimiento.getId(), ProyectoAreaConocimiento.class);
    AssertHelper.idNotNull(proyectoAreaConocimiento.getProyectoId(), Proyecto.class);

    ProyectoAreaConocimiento returnValue = repository.save(proyectoAreaConocimiento);

    log.debug("create(ProyectoAreaConocimiento proyectoAreaConocimiento) - end");
    return returnValue;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ProyectoAreaConocimiento.class);

    if (!repository.existsById(id)) {
      throw new ProyectoAreaConocimientoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  @Override
  public Page<ProyectoAreaConocimiento> findAllByProyectoId(Long proyectoId, String query, Pageable paging) {
    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoAreaConocimiento> specs = ProyectoAreaConocimientoSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<ProyectoAreaConocimiento> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;

  }

}
