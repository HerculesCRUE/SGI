package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoResponsableEconomicoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoResponsableEconomicoOverlapRangeException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoResponsableEconomicoProjectRangeException;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoResponsableEconomicoSpecifications;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link ProyectoResponsableEconomico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProyectoResponsableEconomicoService {

  private final Validator validator;

  private final ProyectoResponsableEconomicoRepository repository;
  private final ProyectoRepository proyectoRepository;

  public ProyectoResponsableEconomicoService(Validator validator,
      ProyectoResponsableEconomicoRepository proyectoResponsableEconomicoRepository,
      ProyectoRepository proyectoRepository) {
    this.validator = validator;
    this.repository = proyectoResponsableEconomicoRepository;
    this.proyectoRepository = proyectoRepository;
  }

  /**
   * Actualiza la lista de {@link ProyectoEquipo} del {@link Proyecto}, elimina
   * los que no están en la lista actual pero tienen el mismo proyectoId , añade
   * los que no tienen id y actualiza los ya existentes.
   * 
   * @param proyectoId             Identificador del {@link Proyecto}
   * @param responsablesEconomicos la lista con los cambios para aplicar.
   * @return La lista actualizada de {@link ProyectoEquipo}.
   */
  @Transactional
  @Validated({ ProyectoResponsableEconomico.OnActualizar.class })
  public List<ProyectoResponsableEconomico> updateProyectoResponsableEconomicos(Long proyectoId,
      List<ProyectoResponsableEconomico> responsablesEconomicos) {
    log.debug("updateProyectoResponsableEconomicos(List<ProyectoResponsableEconomico> responsablesEconomicos) - start");

    Proyecto proyecto = proyectoRepository.findById(proyectoId)
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoId));

    List<ProyectoResponsableEconomico> responsablesEconomicosBD = repository.findByProyectoId(proyectoId);

    // ProyectoResponsableEconomico NO encontrados
    responsablesEconomicos.stream().forEach(responsableEconomico -> {
      if (responsableEconomico.getId() != null && responsablesEconomicosBD.stream()
          .map(ProyectoResponsableEconomico::getId).noneMatch(id -> id.equals(responsableEconomico.getId()))) {
        throw new ProyectoResponsableEconomicoNotFoundException(responsableEconomico.getId());
      }
    });

    // ProyectoResponsableEconomico eliminados
    List<ProyectoResponsableEconomico> responsablesEconomicosEliminar = responsablesEconomicosBD
        .stream().filter(responsableEconomico -> responsablesEconomicos.stream()
            .map(ProyectoResponsableEconomico::getId).noneMatch(id -> id.equals(responsableEconomico.getId())))
        .collect(Collectors.toList());

    if (!responsablesEconomicosEliminar.isEmpty()) {
      repository.deleteAll(responsablesEconomicosEliminar);
    }

    if (responsablesEconomicos.isEmpty()) {
      return new ArrayList<>();
    }

    // Ordena los responsables por fechaInicial
    responsablesEconomicos.sort(Comparator.comparing(ProyectoResponsableEconomico::getFechaInicio));

    Instant lastEnd = null;
    boolean emptyFechaInicio = false;
    boolean emptyFechaFin = false;
    for (ProyectoResponsableEconomico responsableEconomico : responsablesEconomicos) {

      Assert.notNull(responsableEconomico.getProyectoId(),
          () -> ProblemMessage.builder().key(Assert.class, "notNull")
              .parameter("field", ApplicationContextSupport.getMessage("id"))
              .parameter("entity", ApplicationContextSupport.getMessage(Proyecto.class)).build());

      Assert.notNull(responsableEconomico.getPersonaRef(),
          () -> ProblemMessage.builder().key(Assert.class, "notNull")
              .parameter("field", ApplicationContextSupport.getMessage("proyectoResponsableEconomico.personaRef"))
              .parameter("entity", ApplicationContextSupport.getMessage(ProyectoResponsableEconomico.class)).build());

      Instant fechaFinProyecto = proyecto.getFechaFinDefinitiva() != null ? proyecto.getFechaFinDefinitiva()
          : proyecto.getFechaFin();

      if (emptyFechaInicio && responsableEconomico.getFechaInicio() == null) {
        // Solo puede haber un registro con la fecha de inicio vacia
        throw new ProyectoResponsableEconomicoOverlapRangeException();
      }
      if (emptyFechaFin && responsableEconomico.getFechaFin() == null) {
        // Solo puede haber un registro con la fecha de fin vacia
        throw new ProyectoResponsableEconomicoOverlapRangeException();
      }
      if (!emptyFechaInicio && responsableEconomico.getFechaInicio() == null) {
        emptyFechaInicio = true;
      }
      if (!emptyFechaFin && responsableEconomico.getFechaFin() == null) {
        emptyFechaFin = true;
      }

      if ((responsableEconomico.getFechaInicio() != null
          && responsableEconomico.getFechaInicio().isBefore(proyecto.getFechaInicio()))
          || (responsableEconomico.getFechaFin() != null
              && responsableEconomico.getFechaFin().isAfter(fechaFinProyecto))) {
        throw new ProyectoResponsableEconomicoProjectRangeException(responsableEconomico.getFechaInicio(),
            fechaFinProyecto);
      }

      if (lastEnd != null && responsableEconomico.getFechaInicio() != null
          && responsableEconomico.getFechaInicio().isBefore(lastEnd)) {
        // La fecha de inicio no puede ser anterior a la fecha fin del anterior elemento
        throw new ProyectoResponsableEconomicoOverlapRangeException();
      }

      Set<ConstraintViolation<ProyectoResponsableEconomico>> result = validator.validate(responsableEconomico,
          ProyectoResponsableEconomico.OnActualizar.class);

      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      lastEnd = responsableEconomico.getFechaFin() != null ? responsableEconomico.getFechaFin() : fechaFinProyecto;
    }

    List<ProyectoResponsableEconomico> returnValue = repository.saveAll(responsablesEconomicos);

    log.debug("updateProyectoResponsableEconomicos(List<ProyectoResponsableEconomico> responsablesEconomicos) - start");
    return returnValue;
  }

  /**
   * Obtiene los {@link ProyectoResponsableEconomico} para una {@link Proyecto}.
   *
   * @param proyectoId el id de la {@link Proyecto}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoResponsableEconomico} de la
   *         {@link Proyecto} paginadas.
   */
  public Page<ProyectoResponsableEconomico> findAllByProyecto(Long proyectoId, String query, Pageable pageable) {
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - start");
    Specification<ProyectoResponsableEconomico> specs = ProyectoResponsableEconomicoSpecifications
        .byProyectoId(proyectoId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ProyectoResponsableEconomico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyecto(Long proyectoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ProyectoResponsableEconomico} por su id.
   *
   * @param id el id de la entidad {@link ProyectoResponsableEconomico}.
   * @return la entidad {@link ProyectoResponsableEconomico}.
   */
  public ProyectoResponsableEconomico findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoResponsableEconomico returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoResponsableEconomicoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
