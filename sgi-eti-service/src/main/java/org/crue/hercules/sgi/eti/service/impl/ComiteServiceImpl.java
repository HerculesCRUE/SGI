package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.specification.ComiteSpecifications;
import org.crue.hercules.sgi.eti.service.ComiteService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Comite}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ComiteServiceImpl implements ComiteService {

  private final ComiteRepository comiteRepository;

  public ComiteServiceImpl(ComiteRepository comiteRepository) {
    this.comiteRepository = comiteRepository;
  }

  /**
   * Obtiene todas las entidades {@link Comite} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Comite} paginadas y filtradas.
   */
  public Page<Comite> findAll(String query, Pageable paging) {
    log.debug("findAllComite(String query,Pageable paging) - start");
    Specification<Comite> specs = ComiteSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Comite> returnValue = comiteRepository.findAll(specs, paging);
    log.debug("findAllComite(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Optiene una entidad {@link Comite} por id.
   *
   * @param id el id de la entidad {@link Comite}.
   * @return la entidad {@link Comite}.
   * @throws ComiteNotFoundException excepción.
   */
  public Comite findById(final Long id) throws ComiteNotFoundException {
    log.debug("Petición a get Comite : {}  - start", id);
    Comite comite = comiteRepository.findById(id).orElseThrow(() -> new ComiteNotFoundException(id));
    log.debug("Petición a get Comite : {}  - end", id);
    return comite;

  }
}