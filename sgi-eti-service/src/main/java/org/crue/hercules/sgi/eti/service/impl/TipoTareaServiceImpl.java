package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.exceptions.TipoTareaNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.repository.TipoTareaRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoTareaSpecifications;
import org.crue.hercules.sgi.eti.service.TipoTareaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoTarea}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoTareaServiceImpl implements TipoTareaService {
  private final TipoTareaRepository tipoTareaRepository;

  public TipoTareaServiceImpl(TipoTareaRepository tipoTareaRepository) {
    this.tipoTareaRepository = tipoTareaRepository;
  }

  /**
   * Obtiene todas las entidades {@link TipoTarea} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoTarea} paginadas y filtradas.
   */
  public Page<TipoTarea> findAll(String query, Pageable paging) {
    log.debug("findAllTipoTarea(String query, Pageable paging) - start");
    Specification<TipoTarea> specs = TipoTareaSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoTarea> returnValue = tipoTareaRepository.findAll(specs, paging);
    log.debug("findAllTipoTarea(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoTarea} por id.
   *
   * @param id el id de la entidad {@link TipoTarea}.
   * @return la entidad {@link TipoTarea}.
   * @throws TipoTareaNotFoundException Si no existe ningún {@link TipoTarea} con
   *                                    ese id.
   */
  public TipoTarea findById(final Long id) throws TipoTareaNotFoundException {
    log.debug("Petición a get TipoTarea : {}  - start", id);
    final TipoTarea TipoTarea = tipoTareaRepository.findById(id).orElseThrow(() -> new TipoTareaNotFoundException(id));
    log.debug("Petición a get TipoTarea : {}  - end", id);
    return TipoTarea;

  }
}
