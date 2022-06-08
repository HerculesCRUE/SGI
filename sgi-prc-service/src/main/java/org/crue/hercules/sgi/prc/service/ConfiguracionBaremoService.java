package org.crue.hercules.sgi.prc.service;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.model.ConfiguracionBaremo;
import org.crue.hercules.sgi.prc.repository.ConfiguracionBaremoRepository;
import org.crue.hercules.sgi.prc.repository.specification.ConfiguracionBaremoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link ConfiguracionBaremo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConfiguracionBaremoService {

  private final ConfiguracionBaremoRepository repository;

  public ConfiguracionBaremoService(ConfiguracionBaremoRepository configuracionBaremoRepository) {
    this.repository = configuracionBaremoRepository;
  }

  /**
   * Obtener todas las entidades {@link ConfiguracionBaremo} activas paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ConfiguracionBaremo} activas paginadas
   *         y/o filtradas.
   */
  public Page<ConfiguracionBaremo> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<ConfiguracionBaremo> specs = ConfiguracionBaremoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConfiguracionBaremo> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }
}
