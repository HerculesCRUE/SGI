package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.RequerimientoJustificacionNotDeleteableException;
import org.crue.hercules.sgi.csp.exceptions.RequerimientoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion_;
import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.crue.hercules.sgi.csp.repository.RequerimientoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.RequerimientoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RequerimientoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequerimientoJustificacionService {

  private final Validator validator;
  private final RequerimientoJustificacionRepository repository;
  private final IncidenciaDocumentacionRequerimientoService incidenciaDocumentacionRequerimientoService;
  private final GastoRequerimientoJustificacionService gastoRequerimientoJustificacionService;
  private final AlegacionRequerimientoService alegacionRequerimientoService;
  private final ProyectoPeriodoJustificacionSeguimientoService proyectoPeriodoJustificacionSeguimientoService;

  /**
   * Obtener todas las entidades {@link RequerimientoJustificacion} pertenecientes
   * al ProyectoSgeRef paginadas y/o filtradas.
   *
   * @param proyectoSgeRef el identificador de un ProyectoSGE
   * @param pageable       la información de la paginación.
   * @param query          la información del filtro.
   * @return la lista de entidades {@link RequerimientoJustificacion} paginadas
   *         y/o filtradas.
   */
  public Page<RequerimientoJustificacion> findAllByProyectoSgeRef(String proyectoSgeRef, String query,
      Pageable pageable) {
    log.debug("findAllByProyectoSgeRef(String proyectoSgeRef, String query, Pageable pageable) - start");
    Specification<RequerimientoJustificacion> specs = RequerimientoJustificacionSpecifications
        .byProyectoProyectoSgeProyectoSgeRef(proyectoSgeRef).and(SgiRSQLJPASupport.toSpecification(query));

    Page<RequerimientoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoSgeRef(String proyectoSgeRef, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link RequerimientoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link RequerimientoJustificacion}.
   * @return la entidad {@link RequerimientoJustificacion}.
   */
  public RequerimientoJustificacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    AssertHelper.idNotNull(id, RequerimientoJustificacion.class);

    final RequerimientoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new RequerimientoJustificacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la entidad {@link RequerimientoJustificacion}.
   *
   * @param id identificador de la entidad {@link RequerimientoJustificacion}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    AssertHelper.idNotNull(id, RequerimientoJustificacion.class);

    if (repository.count(RequerimientoJustificacionSpecifications.byRequerimientoPrevioId(id)) > 0) {
      throw new RequerimientoJustificacionNotDeleteableException();
    }

    RequerimientoJustificacion requerimientoJustificacionToDelete = findById(id);

    // Delete related entities
    incidenciaDocumentacionRequerimientoService.deleteByRequerimientoJustificacionId(id);
    gastoRequerimientoJustificacionService.deleteByRequerimientoJustificacionId(id);
    alegacionRequerimientoService.deleteByRequerimientoJustificacionId(id);

    // Delete main entity
    repository.delete(requerimientoJustificacionToDelete);

    recalcularNumRequerimiento(requerimientoJustificacionToDelete.getProyectoProyectoSgeId());

    // Si al borrar el requerimiento, este estaba relacionado con un periodo de
    // justificacion, y no quedan mas relacionados con dicho periodo de
    // justificacion: borramos el seguimiento asociado a dicho periodo
    if (requerimientoJustificacionToDelete.getProyectoPeriodoJustificacionId() != null &&
        !existsAnyByProyectoPeriodoJustificacionId(
            requerimientoJustificacionToDelete.getProyectoPeriodoJustificacionId())) {
      proyectoPeriodoJustificacionSeguimientoService.deleteByProyectoPeriodoJustificacionId(
          requerimientoJustificacionToDelete.getProyectoPeriodoJustificacionId());
    }

    log.debug("deleteById(Long id) - end");
  }

  /**
   * Guardar un nuevo {@link RequerimientoJustificacion}.
   *
   * @param requerimientoJustificacion la entidad
   *                                   {@link RequerimientoJustificacion} a
   *                                   guardar.
   * @return la entidad {@link RequerimientoJustificacion} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public RequerimientoJustificacion create(@Valid RequerimientoJustificacion requerimientoJustificacion) {
    log.debug("create(RequerimientoJustificacion requerimientoJustificacion) - start");
    AssertHelper.idIsNull(requerimientoJustificacion.getId(), RequerimientoJustificacion.class);

    // numRequerimiento no puede ser null y el valor se va a recalcular despues
    requerimientoJustificacion.setNumRequerimiento(0);
    RequerimientoJustificacion returnValue = repository.save(requerimientoJustificacion);

    recalcularNumRequerimiento(returnValue.getProyectoProyectoSgeId());

    log.debug("create(RequerimientoJustificacion requerimientoJustificacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link RequerimientoJustificacion}.
   *
   * @param requerimientoJustificacion la entidad
   *                                   {@link RequerimientoJustificacion}
   *                                   a actualizar.
   * @return la entidad {@link RequerimientoJustificacion} persistida.
   */
  @Transactional
  public RequerimientoJustificacion update(RequerimientoJustificacion requerimientoJustificacion) {
    log.debug("update(RequerimientoJustificacion requerimientoJustificacion) - start");

    AssertHelper.idNotNull(requerimientoJustificacion.getId(), RequerimientoJustificacion.class);
    Assert.notNull(requerimientoJustificacion.getTipoRequerimiento(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter(AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport.getMessage(TipoRequerimiento.class))
            .parameter(AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(RequerimientoJustificacion.class))
            .build());
    AssertHelper.idNotNull(requerimientoJustificacion.getTipoRequerimiento().getId(), TipoRequerimiento.class);

    return repository.findById(requerimientoJustificacion.getId()).map(requerimientoJustificacionExistente -> {

      if (!requerimientoJustificacionExistente.getTipoRequerimiento().getId()
          .equals(requerimientoJustificacion.getTipoRequerimiento().getId())) {
        // Si estamos mofificando el TipoRequerimiento invocar validaciones asociadas
        // a OnActualizarTipoRequerimiento
        Set<ConstraintViolation<RequerimientoJustificacion>> result = validator.validate(requerimientoJustificacion,
            RequerimientoJustificacion.OnActualizarTipoRequerimiento.class);
        if (!result.isEmpty()) {
          throw new ConstraintViolationException(result);
        }
      }

      // Establecemos los campos actualizables con los recibidos
      requerimientoJustificacionExistente.setAnticipoJustificado(requerimientoJustificacion.getAnticipoJustificado());
      requerimientoJustificacionExistente.setDefectoAnticipo(requerimientoJustificacion.getDefectoAnticipo());
      requerimientoJustificacionExistente.setDefectoSubvencion(requerimientoJustificacion.getDefectoSubvencion());
      requerimientoJustificacionExistente.setFechaFinAlegacion(requerimientoJustificacion.getFechaFinAlegacion());
      requerimientoJustificacionExistente.setFechaNotificacion(requerimientoJustificacion.getFechaNotificacion());
      requerimientoJustificacionExistente.setImporteAceptado(requerimientoJustificacion.getImporteAceptado());
      requerimientoJustificacionExistente.setImporteAceptadoCd(requerimientoJustificacion.getImporteAceptadoCd());
      requerimientoJustificacionExistente.setImporteAceptadoCi(requerimientoJustificacion.getImporteAceptadoCi());
      requerimientoJustificacionExistente.setImporteRechazado(requerimientoJustificacion.getImporteRechazado());
      requerimientoJustificacionExistente.setImporteRechazadoCd(requerimientoJustificacion.getImporteRechazadoCd());
      requerimientoJustificacionExistente.setImporteRechazadoCi(requerimientoJustificacion.getImporteRechazadoCi());
      requerimientoJustificacionExistente.setImporteReintegrar(requerimientoJustificacion.getImporteReintegrar());
      requerimientoJustificacionExistente.setImporteReintegrarCd(requerimientoJustificacion.getImporteReintegrarCd());
      requerimientoJustificacionExistente.setImporteReintegrarCi(requerimientoJustificacion.getImporteReintegrarCi());
      requerimientoJustificacionExistente.setInteresesReintegrar(requerimientoJustificacion.getInteresesReintegrar());
      requerimientoJustificacionExistente.setObservaciones(requerimientoJustificacion.getObservaciones());
      requerimientoJustificacionExistente
          .setProyectoPeriodoJustificacionId(requerimientoJustificacion.getProyectoPeriodoJustificacionId());
      requerimientoJustificacionExistente
          .setProyectoProyectoSgeId(requerimientoJustificacion.getProyectoProyectoSgeId());
      requerimientoJustificacionExistente.setRecursoEstimado(requerimientoJustificacion.getRecursoEstimado());
      requerimientoJustificacionExistente
          .setRequerimientoPrevioId(requerimientoJustificacion.getRequerimientoPrevioId());
      requerimientoJustificacionExistente
          .setSubvencionJustificada(requerimientoJustificacion.getSubvencionJustificada());
      requerimientoJustificacionExistente.setTipoRequerimiento(requerimientoJustificacion.getTipoRequerimiento());

      // Actualizamos la entidad
      RequerimientoJustificacion returnValue = repository.save(requerimientoJustificacionExistente);

      recalcularNumRequerimiento(returnValue.getProyectoProyectoSgeId());
      log.debug("update(RequerimientoJustificacion requerimientoJustificacion) - end");
      return returnValue;
    }).orElseThrow(() -> new RequerimientoJustificacionNotFoundException(requerimientoJustificacion.getId()));
  }

  /**
   * Actualiza el número de requerimiento en función de la fecha de notificación
   * de los requerimientos del {@link Proyecto} relacionado con el
   * {@link ProyectoProyectoSge}.
   * 
   * @param proyectoProyectoSgeId identificador del {@link ProyectoProyectoSge}
   */
  private void recalcularNumRequerimiento(Long proyectoProyectoSgeId) {
    List<RequerimientoJustificacion> requerimientosJustificacion = repository
        .findAll(RequerimientoJustificacionSpecifications
            .byProyectoIdRelatedToProyectoProyectoSgeId(proyectoProyectoSgeId),
            Sort.by(Sort.Direction.ASC, RequerimientoJustificacion_.FECHA_NOTIFICACION));

    AtomicInteger numPeriodo = new AtomicInteger(0);

    for (RequerimientoJustificacion requerimientoJustificacion : requerimientosJustificacion) {
      // Actualiza el numero de requerimiento
      requerimientoJustificacion.setNumRequerimiento(numPeriodo.incrementAndGet());
    }

    repository.saveAll(requerimientosJustificacion);
  }

  /**
   * Obtener todas las entidades {@link RequerimientoJustificacion} pertenecientes
   * al {@link Proyecto} paginadas y/o filtradas.
   *
   * @param proyectoId el identificador de un {@link Proyecto}
   * @param pageable   la información de la paginación.
   * @param query      la información del filtro.
   * @return la lista de entidades {@link RequerimientoJustificacion} paginadas
   *         y/o filtradas.
   */
  public Page<RequerimientoJustificacion> findAllByProyectoId(Long proyectoId, String query,
      Pageable pageable) {
    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - start");
    Specification<RequerimientoJustificacion> specs = RequerimientoJustificacionSpecifications
        .byProyectoId(proyectoId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<RequerimientoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoId(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba si existe algun {@link RequerimientoJustificacion} vinculado a la
   * entidad {@link ProyectoPeriodoJustificacion}.
   * 
   * @param proyectoPeriodoJustificacionId id de la entidad
   *                                       {@link ProyectoPeriodoJustificacion}.
   * @return true si existe una o mas / false en caso contrario.
   */
  public boolean existsAnyByProyectoPeriodoJustificacionId(Long proyectoPeriodoJustificacionId) {
    log.debug("existAnyByProyectoPeriodoJustificacionId(Long proyectoPeriodoJustificacionId) - start");
    boolean existAny = repository.count(
        RequerimientoJustificacionSpecifications.byProyectoPeriodoJustificacionId(proyectoPeriodoJustificacionId)) > 0;
    log.debug("existAnyByProyectoPeriodoJustificacionId(Long proyectoPeriodoJustificacionId) - end");
    return existAny;
  }
}
