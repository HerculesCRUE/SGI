package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.model.ProyectoIVA;
import org.crue.hercules.sgi.csp.repository.ProyectoIVARepository;
import org.crue.hercules.sgi.csp.service.ProyectoIVAService;
import org.crue.hercules.sgi.csp.util.ProyectoHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ProyectoIVA}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoIVAServiceImpl implements ProyectoIVAService {

  private final ProyectoIVARepository repository;
  private final ProyectoHelper proyectoHelper;

  public ProyectoIVAServiceImpl(ProyectoIVARepository repository, ProyectoHelper proyectoHelper) {
    this.repository = repository;
    this.proyectoHelper = proyectoHelper;
  }

  /**
   * Guarda la entidad {@link ProyectoIVA}.
   * 
   * @param proyectoIVA la entidad {@link ProyectoIVA} a guardar.
   * @return ProyectoIVA la entidad {@link ProyectoIVA} persistida.
   */
  @Override
  @Transactional
  public ProyectoIVA create(ProyectoIVA proyectoIVA) {
    log.debug("create(ProyectoIVA proyectoIVA) - start");

    ProyectoIVA returnValue = repository.save(proyectoIVA);

    log.debug("create(ProyectoIVA proyectoIVA) - end");
    return returnValue;

  }

  @Override
  public Page<ProyectoIVA> findAllByProyectoIdOrderByIdDesc(Long proyectoId, Pageable pageable) {
    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - start");

    proyectoHelper.checkCanAccessProyecto(proyectoId);

    Page<ProyectoIVA> returnValue = repository.findAllByProyectoIdAndIvaIsNotNullOrderByIdDesc(proyectoId, pageable);

    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
