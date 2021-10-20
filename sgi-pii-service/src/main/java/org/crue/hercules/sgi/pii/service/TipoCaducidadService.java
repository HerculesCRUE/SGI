package org.crue.hercules.sgi.pii.service;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.pii.model.TipoCaducidad;
import org.crue.hercules.sgi.pii.repository.TipoCaducidadRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class TipoCaducidadService {

  private final TipoCaducidadRepository tipoCaducidadRepository;

  /**
   * Obtener todas las entidades {@link TipoCaducidad} paginadas y/o filtradas.
   *
   * @param pageable Información de la paginación.
   * @param query    Información del/los filtros a aplicar.
   * @return Lista de entidades {@link TipoCaducidad} paginadas y/o filtradas.
   */
  public Page<TipoCaducidad> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoCaducidad> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoCaducidad> returnValue = this.tipoCaducidadRepository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

}
