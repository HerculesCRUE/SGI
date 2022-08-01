package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.crue.hercules.sgi.csp.repository.TipoRequerimientoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoRequerimientoSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link TipoRequerimiento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipoRequerimientoService {

  private final TipoRequerimientoRepository repository;

  /**
   * Obtener todas las entidades {@link TipoRequerimiento} paginadas y/o
   * filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link TipoRequerimiento} paginadas y/o
   *         filtradas.
   */
  public Page<TipoRequerimiento> findActivos(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<TipoRequerimiento> specs = TipoRequerimientoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<TipoRequerimiento> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }
}
