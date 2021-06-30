package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotalConceptoGasto;
import org.crue.hercules.sgi.csp.dto.SolicitudProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoPresupuestoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoPresupuestoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoPresupuestoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudProyectoPresupuesto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoPresupuestoServiceImpl implements SolicitudProyectoPresupuestoService {

  private final SolicitudProyectoPresupuestoRepository repository;
  private final SolicitudService solicitudService;
  private final SolicitudProyectoRepository solicitudProyectoRepository;

  public SolicitudProyectoPresupuestoServiceImpl(SolicitudProyectoPresupuestoRepository repository,
      SolicitudService solicitudService, SolicitudProyectoRepository solicitudProyectoRepository) {
    this.repository = repository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
  }

  /**
   * Guarda la entidad {@link SolicitudProyectoPresupuesto}.
   * 
   * @param solicitudProyectoPresupuesto la entidad
   *                                     {@link SolicitudProyectoPresupuesto} a
   *                                     guardar.
   * @return SolicitudProyectoPresupuesto la entidad
   *         {@link SolicitudProyectoPresupuesto} persistida.
   */
  @Override
  @Transactional
  public SolicitudProyectoPresupuesto create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) {
    log.debug("create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - start");

    Assert.isNull(solicitudProyectoPresupuesto.getId(),
        "Id tiene que ser null para crear la SolicitudProyectoPresupuesto");

    SolicitudProyectoPresupuesto returnValue = repository.save(solicitudProyectoPresupuesto);

    log.debug("create(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoPresupuesto}.
   * 
   * @param solicitudProyectoPresupuesto solicitudProyectoPresupuesto
   *                                     {@link SolicitudProyectoPresupuesto} con
   *                                     los datos actualizados.
   * @return {@link SolicitudProyectoPresupuesto} actualizado.
   */
  @Override
  @Transactional
  public SolicitudProyectoPresupuesto update(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) {
    log.debug("update(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - start");

    Assert.notNull(solicitudProyectoPresupuesto.getId(),
        "Id no puede ser null para actualizar SolicitudProyectoPresupuesto");

    // comprobar si la solicitud es modificable
    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository
        .findById(solicitudProyectoPresupuesto.getSolicitudProyectoId()).orElseThrow(
            () -> new SolicitudProyectoNotFoundException(solicitudProyectoPresupuesto.getSolicitudProyectoId()));
    Assert.isTrue(solicitudService.modificable(solicitudProyecto.getId()),
        "No se puede modificar SolicitudProyectoPresupuesto");

    return repository.findById(solicitudProyectoPresupuesto.getId()).map((solicitudProyectoPresupuestoExistente) -> {

      solicitudProyectoPresupuestoExistente.setAnualidad(solicitudProyectoPresupuesto.getAnualidad());
      solicitudProyectoPresupuestoExistente.setImporteSolicitado(solicitudProyectoPresupuesto.getImporteSolicitado());
      solicitudProyectoPresupuestoExistente
          .setImportePresupuestado(solicitudProyectoPresupuesto.getImportePresupuestado());
      solicitudProyectoPresupuestoExistente.setObservaciones(solicitudProyectoPresupuesto.getObservaciones());

      SolicitudProyectoPresupuesto returnValue = repository.save(solicitudProyectoPresupuestoExistente);

      log.debug("update(SolicitudProyectoPresupuesto solicitudProyectoPresupuesto) - end");
      return returnValue;
    }).orElseThrow(() -> new SolicitudProyectoPresupuestoNotFoundException(solicitudProyectoPresupuesto.getId()));
  }

  /**
   * Obtiene una entidad {@link SolicitudProyectoPresupuesto} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoPresupuesto}.
   * @return SolicitudProyectoPresupuesto la entidad
   *         {@link SolicitudProyectoPresupuesto}.
   */
  @Override
  public SolicitudProyectoPresupuesto findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoPresupuesto returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoPresupuestoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina la {@link SolicitudProyectoPresupuesto}.
   *
   * @param id Id del {@link SolicitudProyectoPresupuesto}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "SolicitudProyectoPresupuesto id no puede ser null para eliminar un SolicitudProyectoPresupuesto");
    if (!repository.existsById(id)) {
      throw new SolicitudProyectoPresupuestoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene las {@link SolicitudProyectoPresupuesto} para un {@link Solicitud}.
   *
   * @param solicitudId el id del {@link Solicitud}.
   * @param query       la información del filtro.
   * @param paging      la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoPresupuesto} del
   *         {@link Solicitud} paginadas.
   */
  @Override
  public Page<SolicitudProyectoPresupuesto> findAllBySolicitud(Long solicitudId, String query, Pageable paging) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoPresupuesto> specs = SolicitudProyectoPresupuestoSpecifications
        .bySolicitudId(solicitudId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoPresupuesto> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Recupera la lista paginada de {@link SolicitudProyectoPresupuesto} de una
   * entidad en una {@link Solicitud}.
   * 
   * @param solicitudId Identificador de la {@link Solicitud}.
   * @param entidadRef  Identificador de la entidad.
   * @param ajena       es o no financiacionAjena.
   * @param query       parámentros de búsqueda.
   * @param paging      parámetros de paginación.
   * @return lista paginada.
   */
  @Override
  public Page<SolicitudProyectoPresupuesto> findAllBySolicitudAndEntidadRef(Long solicitudId, String entidadRef,
      boolean ajena, String query, Pageable paging) {
    log.debug(
        "findAllBySolicitudAndEntidadRef(Long solicitudId, String entidadRef, String query, Pageable paging) - start");

    Specification<SolicitudProyectoPresupuesto> specs = SolicitudProyectoPresupuestoSpecifications
        .bySolicitudId(solicitudId).and(SolicitudProyectoPresupuestoSpecifications.byEntidadRef(entidadRef))
        .and(SolicitudProyectoPresupuestoSpecifications.byFinanciacionAjena(ajena))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoPresupuesto> returnValue = repository.findAll(specs, paging);
    log.debug(
        "findAllBySolicitudAndEntidadRef(Long solicitudId, String entidadRef, String query, Pageable paging) - end");
    return returnValue;
  }

  @Override
  public boolean existsBySolicitudProyectoSolicitudIdAndEntidadRefAndFinanciacionAjena(Long solicitudId,
      String entidadRef, boolean ajena) {
    log.debug(
        "existsBySolicitudProyectoSolicitudIdAndEntidadRefAndFinanciacionAjena(Long solicitudId, String entidadRef, boolean ajena) - start");
    boolean returnValue = repository.existsBySolicitudProyectoSolicitudIdAndEntidadRefAndFinanciacionAjena(solicitudId,
        entidadRef, ajena);
    log.debug(
        "existsBySolicitudProyectoSolicitudIdAndEntidadRefAndFinanciacionAjena(Long solicitudId, String entidadRef, boolean ajena) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link SolicitudProyectoPresupuestoTotales} de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Identificador de la entidad {@link Solicitud}.
   * @return {@link SolicitudProyectoPresupuestoTotales}.
   */
  @Override
  public SolicitudProyectoPresupuestoTotales getTotales(Long solicitudId) {
    log.debug("getTotales(Long solicitudId) - start");
    final SolicitudProyectoPresupuestoTotales returnValue = repository.getTotales(solicitudId);
    log.debug("getTotales(Long solicitudId) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link SolicitudProyectoPresupuestoTotalConceptoGasto} de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Id de la {@link Solicitud}.
   * @return lista de {@link SolicitudProyectoPresupuestoTotalConceptoGasto}.
   */
  @Override
  public List<SolicitudProyectoPresupuestoTotalConceptoGasto> findAllSolicitudProyectoPresupuestoTotalConceptoGastos(
      Long solicitudId) {
    log.debug("findAllSolicitudProyectoPresupuestoTotalConceptoGastos(Long solicitudId) - start");
    final List<SolicitudProyectoPresupuestoTotalConceptoGasto> returnValue = repository
        .getSolicitudProyectoPresupuestoTotalConceptoGastos(solicitudId);
    log.debug("findAllSolicitudProyectoPresupuestoTotalConceptoGastos(Long solicitudId) - end");
    return returnValue;
  }

  /**
   * Obtiene el {@link SolicitudProyectoPresupuesto} de la
   * {@link SolicitudProyecto}.
   * 
   * @param id {@link SolicitudProyecto}.
   * @return {@link SolicitudProyectoPresupuesto}.
   */
  @Override
  public Boolean hasSolicitudPresupuesto(Long id) {
    log.debug("hasSolicitudPresupuesto(Long id) - start");
    final List<SolicitudProyectoPresupuesto> solicitudProyectoPresupuestos = repository.findBySolicitudProyectoId(id);
    Boolean returnValue = CollectionUtils.isNotEmpty(solicitudProyectoPresupuestos);
    log.debug("hasSolicitudPresupuesto(Long id) - end");
    return returnValue;
  }

}