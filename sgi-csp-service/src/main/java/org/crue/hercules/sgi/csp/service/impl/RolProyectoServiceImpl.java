package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.RolProyectoSpecifications;
import org.crue.hercules.sgi.csp.service.RolProyectoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RolProyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RolProyectoServiceImpl implements RolProyectoService {

  private final RolProyectoRepository repository;

  public RolProyectoServiceImpl(RolProyectoRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene una entidad {@link RolProyecto} por id.
   * 
   * @param id Identificador de la entidad {@link RolProyecto}.
   * @return RolProyecto la entidad {@link RolProyecto}.
   */
  @Override
  public RolProyecto findById(Long id) {
    log.debug("findById(Long id) - start");
    final RolProyecto returnValue = repository.findById(id).orElseThrow(() -> new RolProyectoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link RolProyecto} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolProyecto} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<RolProyecto> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<RolProyecto> specs = RolProyectoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<RolProyecto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }
}
