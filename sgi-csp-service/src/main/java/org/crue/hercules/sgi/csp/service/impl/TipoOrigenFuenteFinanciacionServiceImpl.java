package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.repository.TipoOrigenFuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoOrigenFuenteFinanciacionSpecification;
import org.crue.hercules.sgi.csp.service.TipoOrigenFuenteFinanciacionService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link TipoOrigenFuenteFinanciacion}.
 */

@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoOrigenFuenteFinanciacionServiceImpl implements TipoOrigenFuenteFinanciacionService {

  private final TipoOrigenFuenteFinanciacionRepository tipoOrigenFuenteFinanciacionRepository;

  public TipoOrigenFuenteFinanciacionServiceImpl(
      TipoOrigenFuenteFinanciacionRepository tipoOrigenFuenteFinanciacionRepository) {

    this.tipoOrigenFuenteFinanciacionRepository = tipoOrigenFuenteFinanciacionRepository;
  }

  /**
   * Obtener todas las entidades {@link TipoOrigenFuenteFinanciacion} paginadas
   * y/o filtradas.
   *
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link TipoOrigenFuenteFinanciacion} paginadas
   *         y/o filtradas.
   */
  @Override
  public Page<TipoOrigenFuenteFinanciacion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<TipoOrigenFuenteFinanciacion> specs = TipoOrigenFuenteFinanciacionSpecification.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoOrigenFuenteFinanciacion> returnValue = tipoOrigenFuenteFinanciacionRepository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");

    return returnValue;
  }

}
