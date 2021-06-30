package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoClasificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoClasificacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoClasificacionSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoClasificacionService;
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
 * {@link SolicitudProyectoClasificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoClasificacionServiceImpl implements SolicitudProyectoClasificacionService {

  private final SolicitudProyectoClasificacionRepository repository;

  public SolicitudProyectoClasificacionServiceImpl(
      SolicitudProyectoClasificacionRepository solicitudProyectoClasificacionRepository) {
    this.repository = solicitudProyectoClasificacionRepository;

  }

  /**
   * Guardar un nuevo {@link SolicitudProyectoClasificacion}.
   *
   * @param solicitudProyectoClasificacion la entidad
   *                                       {@link SolicitudProyectoClasificacion}
   *                                       a guardar.
   * @return la entidad {@link SolicitudProyectoClasificacion} persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoClasificacion create(SolicitudProyectoClasificacion solicitudProyectoClasificacion) {
    log.debug("create(SolicitudProyectoClasificacion solicitudProyectoClasificacion) - start");

    Assert.isNull(solicitudProyectoClasificacion.getId(),
        "SolicitudProyectoClasificacion id tiene que ser null para crear un nuevo SolicitudProyectoClasificacion");

    Assert.notNull(solicitudProyectoClasificacion.getSolicitudProyectoId(),
        "Id SolicitudProyecto no puede ser null para crear SolicitudProyectoClasificacion");

    SolicitudProyectoClasificacion returnValue = repository.save(solicitudProyectoClasificacion);

    log.debug("create(SolicitudProyectoClasificacion solicitudProyectoEntidadFinanciadoraAjena) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link SolicitudProyectoClasificacion}.
   *
   * @param id Id del {@link SolicitudProyectoClasificacion}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "SolicitudProyectoClasificacion id no puede ser null para desactivar un SolicitudProyectoClasificacion");

    if (!repository.existsById(id)) {
      throw new SolicitudProyectoClasificacionNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las {@link SolicitudProyectoClasificacion} para una
   * {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoClasificacion} de la
   *         {@link Solicitud} paginadas.
   */
  public Page<SolicitudProyectoClasificacion> findAllBySolicitud(Long solicitudId, String query, Pageable pageable) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable pageable) - start");
    Specification<SolicitudProyectoClasificacion> specs = SolicitudProyectoClasificacionSpecifications
        .bySolicitudId(solicitudId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoClasificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
