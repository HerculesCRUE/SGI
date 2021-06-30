package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.RolSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.repository.RolSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.RolSocioSpecifications;
import org.crue.hercules.sgi.csp.service.RolSocioService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RolSocio}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RolSocioServiceImpl implements RolSocioService {

  private final RolSocioRepository repository;

  public RolSocioServiceImpl(RolSocioRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene una entidad {@link RolSocio} por id.
   * 
   * @param id Identificador de la entidad {@link RolSocio}.
   * @return RolSocio la entidad {@link RolSocio}.
   */
  @Override
  public RolSocio findById(Long id) {
    log.debug("findById(Long id) - start");
    final RolSocio returnValue = repository.findById(id).orElseThrow(() -> new RolSocioNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link RolSocio} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link RolSocio} activas paginadas y
   *         filtradas.
   */
  @Override
  public Page<RolSocio> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<RolSocio> specs = RolSocioSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));
    Page<RolSocio> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

}
