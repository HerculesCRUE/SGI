package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.TipoRegimenConcurrenciaNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoRegimenConcurrenciaSpecifications;
import org.crue.hercules.sgi.csp.service.TipoRegimenConcurrenciaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link TipoRegimenConcurrencia}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoRegimenConcurrenciaServiceImpl implements TipoRegimenConcurrenciaService {

  private final TipoRegimenConcurrenciaRepository repository;

  public TipoRegimenConcurrenciaServiceImpl(TipoRegimenConcurrenciaRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene todas las entidades {@link TipoRegimenConcurrencia} activos paginadas
   * y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoRegimenConcurrencia} paginadas y
   *         filtradas.
   */
  @Override
  public Page<TipoRegimenConcurrencia> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<TipoRegimenConcurrencia> specs = TipoRegimenConcurrenciaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoRegimenConcurrencia> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoRegimenConcurrencia} por id.
   * 
   * @param id Identificador de la entidad {@link TipoRegimenConcurrencia}.
   * @return TipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia}.
   */
  @Override
  public TipoRegimenConcurrencia findById(Long id) {
    log.debug("findById(Long id) - start");
    final TipoRegimenConcurrencia returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoRegimenConcurrenciaNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
