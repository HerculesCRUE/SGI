package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEntidadNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadFinanciadora;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidadFinanciadoraAjena;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEntidadRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoEntidadSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudProyectoEntidad}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SolicitudProyectoEntidadService {

  private final SolicitudProyectoEntidadRepository repository;

  public SolicitudProyectoEntidadService(SolicitudProyectoEntidadRepository repository) {
    this.repository = repository;
  }

  /**
   * Obtiene {@link SolicitudProyectoEntidad} por su id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoEntidad}.
   * @return la entidad {@link SolicitudProyectoEntidad}.
   */
  public SolicitudProyectoEntidad findById(Long id) {
    log.debug("findById(Long id)  - start");
    final SolicitudProyectoEntidad returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoEntidadNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadFinanciadora} para una
   * {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades
   *         {@link SolicitudProyectoEntidadFinanciadoraAjena} de la
   *         {@link Solicitud} paginadas.
   */
  public Page<ConvocatoriaEntidadFinanciadora> findConvocatoriaEntidadFinanciadoraBySolicitud(Long solicitudId,
      String query, Pageable pageable) {
    log.debug("findConvocatoriaEntidadFinanciadoraBySolicitud(Long solicitudId, Pageable pageable) - start");
    Specification<SolicitudProyectoEntidad> specs = SolicitudProyectoEntidadSpecifications.bySolicitudId(solicitudId)
        .and(SolicitudProyectoEntidadSpecifications.isEntidadFinanciadoraConvocatoria())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEntidadFinanciadora> returnValue = repository.findAll(specs, pageable)
        .map(SolicitudProyectoEntidad::getConvocatoriaEntidadFinanciadora);
    log.debug("findConvocatoriaEntidadFinanciadoraBySolicitud(Long solicitudId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudProyectoEntidad} para una {@link Solicitud}
   * correspondientes a un tipo de presupuesto por entidades (entidades
   * financiadoras de la convocatoria y entidades financiadoras ajenas).
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoEntidad} de la
   *         {@link Solicitud} paginadas.
   */
  public Page<SolicitudProyectoEntidad> findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(Long solicitudId,
      String query, Pageable pageable) {
    log.debug("findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(Long solicitudId, Pageable pageable) - start");
    Specification<SolicitudProyectoEntidad> specs = SolicitudProyectoEntidadSpecifications.bySolicitudId(solicitudId)
        .and(SolicitudProyectoEntidadSpecifications.isEntidadFinanciadoraConvocatoria()
            .or(SolicitudProyectoEntidadSpecifications.isEntidadFinanciadoraAjena()))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoEntidad> returnValue = repository.findAll(specs, pageable);
    log.debug("findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(Long solicitudId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudProyectoEntidad} para una {@link Solicitud}
   * correspondientes a un tipo de presupuesto mixto (entidad gestora de la
   * convocatoria y entidades financiadoras ajenas).
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoEntidad} de la
   *         {@link Solicitud} paginadas.
   */
  public Page<SolicitudProyectoEntidad> findSolicitudProyectoEntidadTipoPresupuestoMixto(Long solicitudId, String query,
      Pageable pageable) {
    log.debug("findSolicitudProyectoEntidadTipoPresupuestoMixto(Long solicitudId, Pageable pageable) - start");
    Specification<SolicitudProyectoEntidad> specs = SolicitudProyectoEntidadSpecifications.bySolicitudId(solicitudId)
        .and(SolicitudProyectoEntidadSpecifications.isEntidadGestora()
            .or(SolicitudProyectoEntidadSpecifications.isEntidadFinanciadoraAjena()))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoEntidad> returnValue = repository.findAll(specs, pageable);
    log.debug("findSolicitudProyectoEntidadTipoPresupuestoMixto(Long solicitudId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudProyectoEntidad} para una
   * {@link SolicitudProyectoEntidadFinanciadoraAjena}
   *
   * @param solicitudProyectoEntidadFinanciadoraAjenaId el id de la
   *                                                    {@link SolicitudProyectoEntidadFinanciadoraAjena}.
   * @return la {@link SolicitudProyectoEntidad}
   */
  public SolicitudProyectoEntidad findBySolicitudProyectoEntidadFinanciadoraAjena(
      Long solicitudProyectoEntidadFinanciadoraAjenaId) {
    log.debug("findSolicitudProyectoEntidadTipoPresupuestoPorEntidad(Long solicitudId, Pageable pageable) - start");

    SolicitudProyectoEntidad returnValue = repository
        .findBySolicitudProyectoEntidadFinanciadoraAjenaId(solicitudProyectoEntidadFinanciadoraAjenaId);
    log.debug("findBySolicitudProyectoEntidadFinanciadoraAjena(Long solicitudId, Pageable pageable) - end");
    return returnValue;
  }

}
