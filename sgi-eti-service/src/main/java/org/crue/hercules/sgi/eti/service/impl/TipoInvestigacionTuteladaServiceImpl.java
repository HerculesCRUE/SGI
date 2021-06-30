package org.crue.hercules.sgi.eti.service.impl;

import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.eti.repository.TipoInvestigacionTuteladaRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoInvestigacionTuteladaSpecifications;
import org.crue.hercules.sgi.eti.service.TipoInvestigacionTuteladaService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoInvestigacionTutelada}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoInvestigacionTuteladaServiceImpl implements TipoInvestigacionTuteladaService {
  private final TipoInvestigacionTuteladaRepository tipoInvestigacionTuteladaRepository;

  public TipoInvestigacionTuteladaServiceImpl(TipoInvestigacionTuteladaRepository tipoInvestigacionTuteladaRepository) {
    this.tipoInvestigacionTuteladaRepository = tipoInvestigacionTuteladaRepository;
  }

  /**
   * Obtiene todas las entidades {@link TipoInvestigacionTutelada} paginadas y
   * filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoInvestigacionTutelada} paginadas y
   *         filtradas.
   */
  public Page<TipoInvestigacionTutelada> findAll(String query, Pageable paging) {
    log.debug("findAllTipoInvestigacionTutelada(String query,Pageable paging) - start");
    Specification<TipoInvestigacionTutelada> specs = TipoInvestigacionTuteladaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoInvestigacionTutelada> returnValue = tipoInvestigacionTuteladaRepository.findAll(specs, paging);
    log.debug("findAllTipoInvestigacionTutelada(String query,Pageable paging) - end");
    return returnValue;
  }

}
