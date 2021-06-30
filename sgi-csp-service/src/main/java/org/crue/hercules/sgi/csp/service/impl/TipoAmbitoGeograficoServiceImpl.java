package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.TipoAmbitoGeograficoNotFoundException;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoAmbitoGeograficoSpecifications;
import org.crue.hercules.sgi.csp.service.TipoAmbitoGeograficoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoAmbitoGeografico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoAmbitoGeograficoServiceImpl implements TipoAmbitoGeograficoService {

  private final TipoAmbitoGeograficoRepository repository;

  public TipoAmbitoGeograficoServiceImpl(TipoAmbitoGeograficoRepository tipoAmbitoGeograficoRepository) {
    this.repository = tipoAmbitoGeograficoRepository;
  }

  /**
   * Obtener todas las entidades {@link TipoAmbitoGeografico} activos paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoAmbitoGeografico} paginadas y/o
   *         filtradas.
   */
  @Override
  public Page<TipoAmbitoGeografico> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoAmbitoGeografico> specs = TipoAmbitoGeograficoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoAmbitoGeografico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoAmbitoGeografico} por su id.
   *
   * @param id el id de la entidad {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico}.
   */
  @Override
  public TipoAmbitoGeografico findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoAmbitoGeografico returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
