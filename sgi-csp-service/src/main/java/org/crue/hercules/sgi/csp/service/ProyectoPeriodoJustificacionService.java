package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionNotDeleteableException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionOverlappedFechasException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionProjectRangeException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinalException;
import org.crue.hercules.sgi.csp.model.EstadoProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.EstadoProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoPeriodoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.modelmapper.internal.util.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoPeriodoJustificacionService {
  private static final String MESSAGE_KEY_IDENTIFICADOR_JUSTIFICACION = "proyectoPeriodoJustificacion.identificadorJustificacion";

  private final Validator validator;

  private final ProyectoPeriodoJustificacionRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final RequerimientoJustificacionService requerimientoJustificacionService;

  private final EstadoProyectoPeriodoJustificacionRepository estadoProyectoPeriodoJustificacionRepository;

  public ProyectoPeriodoJustificacionService(Validator validator,
      ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository,
      ProyectoRepository proyectoRepository,
      RequerimientoJustificacionService requerimientoJustificacionService,
      EstadoProyectoPeriodoJustificacionRepository estadoProyectoPeriodoJustificacionRepository) {
    this.validator = validator;
    this.repository = proyectoPeriodoJustificacionRepository;
    this.proyectoRepository = proyectoRepository;
    this.requerimientoJustificacionService = requerimientoJustificacionService;
    this.estadoProyectoPeriodoJustificacionRepository = estadoProyectoPeriodoJustificacionRepository;
  }

  @Transactional
  @Validated({ ProyectoPeriodoJustificacion.OnActualizar.class })
  public List<ProyectoPeriodoJustificacion> update(Long proyectoId,
      List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificaciones) {
    log.debug(
        "update(Long proyectoPeriodoJustificacionId,List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificaciones) - start");

    if (!proyectoRepository.existsById(proyectoId)) {
      throw new ProyectoNotFoundException(proyectoId);
    }

    List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificacionsBD = repository.findByProyectoId(proyectoId);

    // eliminados
    List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificacionsEliminar = proyectoPeriodoJustificacionsBD.stream()
        .filter(periodo -> proyectoPeriodoJustificaciones.stream().map(ProyectoPeriodoJustificacion::getId)
            .noneMatch(id -> Objects.equals(id, periodo.getId())))
        .collect(Collectors.toList());

    if (!proyectoPeriodoJustificacionsEliminar.isEmpty()) {
      this.deleteAll(proyectoPeriodoJustificacionsEliminar);
    }

    // Ordena los proyecto Periodo Justificacion por fecha de inicio
    proyectoPeriodoJustificaciones.sort(Comparator.comparing(ProyectoPeriodoJustificacion::getFechaInicio,
        Comparator.nullsLast(Comparator.naturalOrder())));

    ProyectoPeriodoJustificacion periodoFinal = proyectoPeriodoJustificaciones.stream()
        .filter(periodo -> periodo.getTipoJustificacion() == TipoJustificacion.FINAL).findFirst().orElse(null);

    AtomicInteger numPeriodo = new AtomicInteger(0);

    // Validaciones
    Proyecto proyecto = proyectoRepository.findById(proyectoId)
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoId));
    Instant fechaInicioProyecto = proyecto.getFechaInicio();
    Instant fechaFinProyecto = proyecto.getFechaFinDefinitiva() != null ? proyecto.getFechaFinDefinitiva()
        : proyecto.getFechaFin();

    List<ProyectoPeriodoJustificacion> returnValue = new ArrayList<>();
    int index = 0;
    ProyectoPeriodoJustificacion periodoJustificacionAnterior = null;
    for (ProyectoPeriodoJustificacion periodoJustificacion : proyectoPeriodoJustificaciones) {

      Optional<ProyectoPeriodoJustificacion> periodoJustificacionBD = proyectoPeriodoJustificacionsBD.stream().filter(
          proyectoPeriodoJustificacionBD -> proyectoPeriodoJustificacionBD.getId().equals(periodoJustificacion.getId()))
          .findFirst();
      // Actualiza el numero de periodo y el proyectoId con el pasado por parametro
      periodoJustificacion.setNumPeriodo(numPeriodo.incrementAndGet());
      periodoJustificacion.setProyectoId(proyectoId);
      // Estos datos solo se actualizan a traves del metodo
      // updateIdentificadorJustificacion
      if (periodoJustificacionBD.isPresent()) {
        periodoJustificacion
            .setIdentificadorJustificacion(periodoJustificacionBD.get().getIdentificadorJustificacion());
        periodoJustificacion
            .setFechaPresentacionJustificacion(periodoJustificacionBD.get().getFechaPresentacionJustificacion());
      }

      // actualizando
      if (periodoJustificacion.getId() != null && proyectoPeriodoJustificacionsBD.stream()
          .noneMatch(periodo -> Objects.equals(periodo.getId(), periodoJustificacion.getId()))) {
        throw new ProyectoPeriodoJustificacionNotFoundException(periodoJustificacion.getId());
      }

      if (fechaInicioProyecto.isAfter(periodoJustificacion.getFechaInicio())
          || fechaFinProyecto.isBefore(periodoJustificacion.getFechaFin())) {
        throw new ProyectoPeriodoJustificacionProjectRangeException(fechaInicioProyecto, fechaFinProyecto);
      }

      if (periodoJustificacionAnterior != null
          && !(periodoJustificacionAnterior.getFechaFin() != null
              && periodoJustificacionAnterior.getFechaFin().isBefore(periodoJustificacion.getFechaInicio()))) {
        throw new ProyectoPeriodoJustificacionOverlappedFechasException();
      }

      // Solo puede haber un tipo de justificacion 'final' y ha de ser el último"
      if (periodoFinal != null
          && !Objects.equals(periodoFinal.getId(), periodoJustificacion.getId())
          && index >= (proyectoPeriodoJustificaciones.size() - 1)) {
        throw new TipoFinalException();
      }

      Set<ConstraintViolation<ProyectoPeriodoJustificacion>> result = validator.validate(periodoJustificacion,
          ProyectoPeriodoJustificacion.OnActualizar.class);

      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      if (Objects.isNull(periodoJustificacion.getId())) {
        // creación
        ProyectoPeriodoJustificacion proyectoPeriodoJustificacionCreado = repository.save(periodoJustificacion);
        periodoJustificacion
            .setEstado(this.createEstadoProyectoPeriodoJustificacionPendiente(
                proyectoPeriodoJustificacionCreado.getId()));
        returnValue.add(proyectoPeriodoJustificacionCreado);
      } else {
        returnValue.add(repository.save(periodoJustificacion));
      }

      periodoJustificacionAnterior = periodoJustificacion;
      index++;
    }

    log.debug(
        "update(Long proyectoPeriodoJustificacionId, List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificaciones) - end");
    return returnValue;
  }

  private EstadoProyectoPeriodoJustificacion createEstadoProyectoPeriodoJustificacionPendiente(
      Long idPeriodoJustificacion) {
    return this.estadoProyectoPeriodoJustificacionRepository.save(EstadoProyectoPeriodoJustificacion.builder()
        .estado(EstadoProyectoPeriodoJustificacion.TipoEstadoPeriodoJustificacion.PENDIENTE)
        .fechaEstado(Instant.now())
        .proyectoPeriodoJustificacionId(idPeriodoJustificacion)
        .build());
  }

  @Transactional
  public void deleteAll(List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificacionesEliminar) {
    for (ProyectoPeriodoJustificacion periodoJustificacionEliminar : proyectoPeriodoJustificacionesEliminar) {
      this.delete(periodoJustificacionEliminar.getId());
    }
  }

  /**
   * Elimina la {@link ProyectoPeriodoJustificacion}.
   *
   * @param id Id del {@link ProyectoPeriodoJustificacion}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id,
        "ProyectoPeriodoJustificacion id no puede ser null para eliminar un ProyectoPeriodoJustificacion");
    if (!repository.existsById(id)) {
      throw new ProyectoPeriodoJustificacionNotFoundException(id);
    }

    if (!checkDeleteable(id)) {
      throw new ProyectoPeriodoJustificacionNotDeleteableException();
    }

    ProyectoPeriodoJustificacion proyectoPeriodoJustificacion = this.findById(id);
    proyectoPeriodoJustificacion.setEstado(null);

    repository.save(proyectoPeriodoJustificacion);

    this.estadoProyectoPeriodoJustificacionRepository.deleteAllByProyectoPeriodoJustificacionId(id);
    repository.deleteById(id);

    log.debug("delete(Long id) - end");

  }

  /**
   * Obtiene una entidad {@link ProyectoPeriodoJustificacion} por id.
   * 
   * @param id Identificador de la entidad {@link ProyectoPeriodoJustificacion}.
   * @return ProyectoPeriodoJustificacion la entidad
   *         {@link ProyectoPeriodoJustificacion}.
   */
  public ProyectoPeriodoJustificacion findById(Long id) {
    log.debug("findById(Long id) - start");
    final ProyectoPeriodoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoPeriodoJustificacionNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene ltodos los {@link ProyectoPeriodoJustificacion} de un proyecto.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link ProyectoPeriodoJustificacion}
   */

  public Page<ProyectoPeriodoJustificacion> findAllByProyectoId(Long proyectoId, String query, Pageable paging) {
    log.debug("findAllByProyectoPeriodoSeguimiento(Long solicitudId, String query, Pageable paging) - start");

    Specification<ProyectoPeriodoJustificacion> specs = ProyectoPeriodoJustificacionSpecifications
        .byProyectoId(proyectoId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoPeriodoJustificacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoPeriodoSeguimiento(Long solicitudId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene ltodos los {@link ProyectoPeriodoJustificacion} del ProyectoSGE
   * filtrados y/o paginados.
   *
   * @param proyectoSgeRef identificador del ProyectoSGE
   * @param query          la información del filtro.
   * @param paging         la información de la paginación.
   * @return la lista de entidades {@link ProyectoPeriodoJustificacion}
   */

  public Page<ProyectoPeriodoJustificacion> findAllByProyectoSgeRef(String proyectoSgeRef, String query,
      Pageable paging) {
    log.debug("findAllByProyectoSgeRef(String proyectoSgeRef, String query, Pageable paging) - start");

    Specification<ProyectoPeriodoJustificacion> specs = ProyectoPeriodoJustificacionSpecifications
        .byProyectoSgeRef(proyectoSgeRef).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoPeriodoJustificacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAllByProyectoSgeRef(String proyectoSgeRef, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el Identificador de Justificación de la entidad
   * {@link ProyectoPeriodoJustificacion}.
   * 
   * @param proyectoPeriodoJustificacion la informacion a modificar.
   * @return la entidad {@link ProyectoPeriodoJustificacion} modificada.
   */
  @Transactional
  public ProyectoPeriodoJustificacion updateIdentificadorJustificacion(
      ProyectoPeriodoJustificacion proyectoPeriodoJustificacion) {
    log.debug("updateIdentificadorJustificacion(ProyectoPeriodoJustificacion proyectoPeriodoJustificacion) - start");
    AssertHelper.idNotNull(proyectoPeriodoJustificacion.getId(), ProyectoPeriodoJustificacion.class);

    return repository.findById(proyectoPeriodoJustificacion.getId()).map(proyectoPeriodoJustificacionToUpdate -> {
      if (StringUtils.compare(proyectoPeriodoJustificacion.getIdentificadorJustificacion(),
          proyectoPeriodoJustificacionToUpdate.getIdentificadorJustificacion()) != 0) {
        Set<ConstraintViolation<ProyectoPeriodoJustificacion>> result = validator.validate(proyectoPeriodoJustificacion,
            ProyectoPeriodoJustificacion.OnActualizarIdentificadorJustificacion.class);

        if (!result.isEmpty()) {
          throw new ConstraintViolationException(result);
        }
      }

      proyectoPeriodoJustificacionToUpdate
          .setIdentificadorJustificacion(proyectoPeriodoJustificacion.getIdentificadorJustificacion());
      proyectoPeriodoJustificacionToUpdate
          .setFechaPresentacionJustificacion(proyectoPeriodoJustificacion.getFechaPresentacionJustificacion());

      ProyectoPeriodoJustificacion returnValue = repository.save(proyectoPeriodoJustificacionToUpdate);

      log.debug("updateIdentificadorJustificacion(ProyectoPeriodoJustificacion proyectoPeriodoJustificacion) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoPeriodoJustificacionNotFoundException(proyectoPeriodoJustificacion.getId()));
  }

  /**
   * Obtiene una entidad {@link ProyectoPeriodoJustificacion} a partir de su
   * identificador de justificacion.
   * 
   * @param identificadorJustificacion de la entidad
   *                                   {@link ProyectoPeriodoJustificacion}
   * @return la entidad {@link ProyectoPeriodoJustificacion}
   */
  public Optional<ProyectoPeriodoJustificacion> findByIdentificadorJustificacion(String identificadorJustificacion) {
    log.debug("findByIdentificadorJustificacion(String identificadorJustificacion) - start");

    AssertHelper.fieldNotNull(identificadorJustificacion, ProyectoPeriodoJustificacion.class,
        MESSAGE_KEY_IDENTIFICADOR_JUSTIFICACION);

    Optional<ProyectoPeriodoJustificacion> returnValue = repository
        .findByIdentificadorJustificacion(identificadorJustificacion);

    log.debug("findByIdentificadorJustificacion(String identificadorJustificacion) - end");
    return returnValue;
  }

  /**
   * Comprueba si una entidad {@link ProyectoPeriodoJustificacion} se puede
   * eliminar.
   * 
   * @param id Identificador de la entidad {@link ProyectoPeriodoJustificacion}.
   * @return true/false
   */
  public boolean checkDeleteable(Long id) {
    log.debug("checkDeleteable(Long id) - start");

    AssertHelper.idNotNull(id, ProyectoPeriodoJustificacion.class);

    boolean isDeleteable = !requerimientoJustificacionService.existsAnyByProyectoPeriodoJustificacionId(id);

    log.debug("checkDeleteable(Long id) - end");
    return isDeleteable;
  }
}
