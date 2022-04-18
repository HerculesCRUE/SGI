package org.crue.hercules.sgi.prc.service;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionLogNotFoundException;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacionLog;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionLogRepository;
import org.crue.hercules.sgi.prc.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para ConvocatoriaBaremacionLog
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class ConvocatoriaBaremacionLogService {

  private final ConvocatoriaBaremacionLogRepository repository;

  /**
   * Obtener todas las entidades {@link ConvocatoriaBaremacionLog} paginadas y/o
   * filtradas.
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link ConvocatoriaBaremacionLog} paginadas y/o
   *         filtradas.
   */
  public Page<ConvocatoriaBaremacionLog> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Specification<ConvocatoriaBaremacionLog> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<ConvocatoriaBaremacionLog> returnValue = repository.findAll(specs, paging);

    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ConvocatoriaBaremacionLog save(Long convocatoriaBaremacionId, String trace) {
    log.debug("save(convocatoriaBaremacionId, log) - start");

    ConvocatoriaBaremacionLog traceLog = ConvocatoriaBaremacionLog.builder()
        .convocatoriaBaremacionId(convocatoriaBaremacionId)
        .trace(trace)
        .build();

    return repository.save(traceLog);
  }

  /**
   * Elimina el {@link ConvocatoriaBaremacionLog}.
   *
   * @param id Id del {@link ConvocatoriaBaremacionLog}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ConvocatoriaBaremacionLog.class);

    if (!repository.existsById(id)) {
      throw new ConvocatoriaBaremacionLogNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  @Transactional
  public void deleteByConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    log.debug("deleteByConvocatoriaBaremacionId(convocatoriaBaremacionId) - start");

    repository.findAllByConvocatoriaBaremacionId(convocatoriaBaremacionId).stream()
        .forEach(convocatoriaBaremacionLog -> delete(convocatoriaBaremacionLog.getId()));

    log.debug("deleteByConvocatoriaBaremacionId(convocatoriaBaremacionId) - end");

  }

}
