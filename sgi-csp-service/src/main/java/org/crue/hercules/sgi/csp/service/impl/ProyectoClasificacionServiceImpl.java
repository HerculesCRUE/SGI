package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ProyectoClasificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoClasificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoClasificacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoClasificacionSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoClasificacionService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de
 * {@link ProyectoClasificacionServiceImpl}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoClasificacionServiceImpl implements ProyectoClasificacionService {

  private final ProyectoClasificacionRepository repository;

  public ProyectoClasificacionServiceImpl(ProyectoClasificacionRepository proyectoClasificacionRepository) {
    this.repository = proyectoClasificacionRepository;

  }

  /**
   * Guardar un nuevo {@link ProyectoClasificacion}.
   *
   * @param proyectoClasificacion la entidad {@link ProyectoClasificacion} a
   *                              guardar.
   * @return la entidad {@link ProyectoClasificacion} persistida.
   */
  @Override
  @Transactional
  public ProyectoClasificacion create(ProyectoClasificacion proyectoClasificacion) {
    log.debug("create(ProyectoClasificacion proyectoClasificacion) - start");

    Assert.isNull(proyectoClasificacion.getId(),
        "ProyectoClasificacion id tiene que ser null para crear un nuevo ProyectoClasificacion");

    Assert.notNull(proyectoClasificacion.getProyectoId(),
        "Id Proyecto no puede ser null para crear ProyectoClasificacion");

    ProyectoClasificacion returnValue = repository.save(proyectoClasificacion);

    log.debug("create(ProyectoClasificacion proyectoClasificacion) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link ProyectoClasificacion}.
   *
   * @param id Id del {@link ProyectoClasificacion}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoClasificacion id no puede ser null para desactivar un ProyectoClasificacion");

    if (!repository.existsById(id)) {
      throw new ProyectoClasificacionNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las {@link ProyectoClasificacion} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoClasificacion} de la
   *         {@link Proyecto} paginadas.
   */
  public Page<ProyectoClasificacion> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoClasificacion> specs = ProyectoClasificacionSpecifications.byProyectoId(proyectoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoClasificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
