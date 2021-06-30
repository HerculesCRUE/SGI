package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.repository.EstadoProyectoRepository;
import org.crue.hercules.sgi.csp.service.EstadoProyectoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link Proyecto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EstadoProyectoServiceImpl implements EstadoProyectoService {

  private final EstadoProyectoRepository repository;

  public EstadoProyectoServiceImpl(EstadoProyectoRepository repository) {
    this.repository = repository;
  }

  /**
   * Guarda la entidad {@link EstadoProyecto}.
   * 
   * @param estadoProyecto la entidad {@link EstadoProyecto} a guardar.
   * @return EstadoProyecto la entidad {@link EstadoProyecto} persistida.
   */
  @Override
  @Transactional
  public EstadoProyecto create(EstadoProyecto estadoProyecto) {
    log.debug("create(EstadoProyecto estadoProyecto) - start");

    Assert.isNull(estadoProyecto.getId(), "EstadoProyecto id tiene que ser null para crear un EstadoProyecto");

    Assert.notNull(estadoProyecto.getProyectoId(), "proyectoId no puede ser null para crear un EstadoProyecto");

    EstadoProyecto returnValue = repository.save(estadoProyecto);

    log.debug("create(EstadoProyecto estadoProyecto) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link EstadoProyecto} para una {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link EstadoProyecto} de la {@link Proyecto}
   *         paginadas.
   */
  @Override
  public Page<EstadoProyecto> findAllByProyecto(Long idProyecto, Pageable paging) {
    log.debug("findAllByProyecto(Long idProyecto, Pageable paging) - start");
    Page<EstadoProyecto> returnValue = repository.findAllByProyectoId(idProyecto, paging);
    log.debug("findAllByProyecto(Long idProyecto, Pageable paging) - end");
    return returnValue;
  }

}
