package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.enums.TipoJustificacion;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoPeriodoJustificacionOverlappedFechasException;
import org.crue.hercules.sgi.csp.exceptions.TipoFinalException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.repository.ProyectoPeriodoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoPeriodoJustificacionSpecifications;
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

  private final Validator validator;

  private final ProyectoPeriodoJustificacionRepository repository;
  private final ProyectoRepository proyectoRepository;

  public ProyectoPeriodoJustificacionService(Validator validator,
      ProyectoPeriodoJustificacionRepository proyectoPeriodoJustificacionRepository,
      ProyectoRepository proyectoRepository) {
    this.validator = validator;
    this.repository = proyectoPeriodoJustificacionRepository;
    this.proyectoRepository = proyectoRepository;
  }

  @Transactional
  @Validated({ ProyectoPeriodoJustificacion.OnActualizar.class })
  public List<ProyectoPeriodoJustificacion> update(Long proyectoId,
      List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificaciones) {
    log.debug(
        "update(Long proyectoPeriodoJustificacionId,List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificaciones) - start");

    proyectoRepository.findById(proyectoId).orElseThrow(() -> new ProyectoNotFoundException(proyectoId));

    List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificacionsBD = repository.findByProyectoId(proyectoId);

    // eliminados
    List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificacionsEliminar = proyectoPeriodoJustificacionsBD.stream()
        .filter(periodo -> !proyectoPeriodoJustificaciones.stream().map(ProyectoPeriodoJustificacion::getId)
            .anyMatch(id -> id == periodo.getId()))
        .collect(Collectors.toList());

    if (!proyectoPeriodoJustificacionsEliminar.isEmpty()) {
      repository.deleteAll(proyectoPeriodoJustificacionsEliminar);
    }

    // Ordena los proyecto Periodo Justificacion por fecha de inicio
    proyectoPeriodoJustificaciones.sort(Comparator.comparing(ProyectoPeriodoJustificacion::getFechaInicio,
        Comparator.nullsLast(Comparator.naturalOrder())));

    ProyectoPeriodoJustificacion periodoFinal = proyectoPeriodoJustificaciones.stream()
        .filter(periodo -> periodo.getTipoJustificacion() == TipoJustificacion.FINAL).findFirst().orElse(null);

    AtomicInteger numPeriodo = new AtomicInteger(0);

    // Validaciones
    List<ProyectoPeriodoJustificacion> returnValue = new ArrayList<ProyectoPeriodoJustificacion>();
    int index = 0;
    for (ProyectoPeriodoJustificacion periodoJustificacion : proyectoPeriodoJustificaciones) {

      // Actualiza el numero de periodo
      periodoJustificacion.setNumPeriodo(numPeriodo.incrementAndGet());

      // Obtiene los rangos no permitidos
      List<Instant[]> rangos = new ArrayList<>();
      proyectoPeriodoJustificaciones.stream().filter(periodo -> periodo != periodoJustificacion).forEach(periodo -> {
        Instant[] rango = { periodo.getFechaInicio(), periodo.getFechaFin() };
        rangos.add(rango);
      });

      // actualizando
      if (periodoJustificacion.getId() != null) {
        proyectoPeriodoJustificacionsBD.stream().filter(periodo -> periodo.getId() == periodoJustificacion.getId())
            .findFirst()
            .orElseThrow(() -> new ProyectoPeriodoJustificacionNotFoundException(periodoJustificacion.getId()));
      }

      // Solo puede haber un tipo de justificacion 'final' y ha de ser el último"
      if ((periodoFinal != null && (periodoFinal.getId() != periodoJustificacion.getId()))
          && periodoJustificacion.getTipoJustificacion().equals(TipoJustificacion.FINAL)
          || (index > proyectoPeriodoJustificaciones.size() - 1)) {
        throw new TipoFinalException();
      }

      Set<ConstraintViolation<ProyectoPeriodoJustificacion>> result = validator.validate(periodoJustificacion,
          ProyectoPeriodoJustificacion.OnActualizar.class);

      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }
      // solapamiento de fechas
      rangos.stream().forEach(rango -> {
        if ((periodoJustificacion.getFechaInicio().isBefore(rango[0])
            && periodoJustificacion.getFechaFin().isBefore(rango[1]))
            || (periodoJustificacion.getFechaInicio().isAfter(rango[0])
                && periodoJustificacion.getFechaFin().isAfter(rango[1]))) {
        } else {
          throw new ProyectoPeriodoJustificacionOverlappedFechasException();
        }
      });
      returnValue.add(repository.save(periodoJustificacion));
    }
    log.debug(
        "update(Long proyectoPeriodoJustificacionId, List<ProyectoPeriodoJustificacion> proyectoPeriodoJustificaciones) - end");
    return returnValue;
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
}
