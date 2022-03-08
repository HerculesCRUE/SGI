package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoResponsableEconomicoMaxMonthException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoResponsableEconomicoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoResponsableEconomicoOverlapRangeException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoClasificacion;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoResponsableEconomico;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoResponsableEconomicoSpecifications;
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
 * Service para gestionar {@link SolicitudProyectoResponsableEconomico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SolicitudProyectoResponsableEconomicoService {

  private final Validator validator;

  private final SolicitudProyectoResponsableEconomicoRepository repository;
  private final SolicitudProyectoRepository solicitudProyectoRepository;

  public SolicitudProyectoResponsableEconomicoService(Validator validator,
      SolicitudProyectoResponsableEconomicoRepository solicitudProyectoResponsableEconomicoRepository,
      SolicitudProyectoRepository solicitudProyectoRepository) {
    this.validator = validator;
    this.repository = solicitudProyectoResponsableEconomicoRepository;
    this.solicitudProyectoRepository = solicitudProyectoRepository;
  }

  /**
   * Actualiza la lista de {@link SolicitudProyectoEquipo} del
   * {@link SolicitudProyecto}, elimina los que no están en la lista actual pero
   * tienen el mismo solicitudProyectoId , añade los que no tienen id y actualiza
   * los ya existentes.
   * 
   * @param solicitudProyectoId    Identificador del {@link SolicitudProyecto}
   * @param responsablesEconomicos la lista con los cambios para aplicar.
   * @return La lista actualizada de {@link SolicitudProyectoEquipo}.
   */
  @Transactional
  @Validated({ SolicitudProyectoResponsableEconomico.OnActualizar.class })
  public List<SolicitudProyectoResponsableEconomico> updateSolicitudProyectoResponsableEconomicos(
      Long solicitudProyectoId, List<SolicitudProyectoResponsableEconomico> responsablesEconomicos) {
    log.debug(
        "updateSolicitudProyectoResponsableEconomicos(List<SolicitudProyectoResponsableEconomico> responsablesEconomicos) - start");

    SolicitudProyecto solicitudProyecto = solicitudProyectoRepository.findById(solicitudProyectoId)
        .orElseThrow(() -> new SolicitudProyectoNotFoundException(solicitudProyectoId));

    List<SolicitudProyectoResponsableEconomico> responsablesEconimicosBD = repository
        .findAllBySolicitudProyectoId(solicitudProyectoId);

    // SolicitudProyectoResponsableEconomico NO encontrados
    responsablesEconomicos.stream().forEach(responsableEconomico -> {
      if (responsableEconomico.getId() != null && responsablesEconimicosBD.stream()
          .map(SolicitudProyectoResponsableEconomico::getId).noneMatch(id -> id.equals(responsableEconomico.getId()))) {
        throw new SolicitudProyectoResponsableEconomicoNotFoundException(responsableEconomico.getId());
      }
    });

    // SolicitudProyectoResponsableEconomico eliminados
    List<SolicitudProyectoResponsableEconomico> responsablesEconomicosEliminar = responsablesEconimicosBD.stream()
        .filter(responsableEconomico -> responsablesEconomicos.stream()
            .map(SolicitudProyectoResponsableEconomico::getId).noneMatch(id -> id.equals(responsableEconomico.getId())))
        .collect(Collectors.toList());

    if (!responsablesEconomicosEliminar.isEmpty()) {
      repository.deleteAll(responsablesEconomicosEliminar);
    }

    if (responsablesEconomicos.isEmpty()) {
      return new ArrayList<>();
    }

    // Ordena los responsables por mesInicial
    responsablesEconomicos.sort(Comparator.comparing(SolicitudProyectoResponsableEconomico::getMesInicio));

    SolicitudProyectoResponsableEconomico responsableEconomicoAnterior = null;
    for (SolicitudProyectoResponsableEconomico responsableEconomico : responsablesEconomicos) {

      Assert.notNull(responsableEconomico.getSolicitudProyectoId(),
          () -> ProblemMessage.builder().key(Assert.class, "notNull")
              .parameter("field", ApplicationContextSupport.getMessage("id"))
              .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProyecto.class)).build());

      Assert.notNull(responsableEconomico.getPersonaRef(), () -> ProblemMessage.builder().key(Assert.class, "notNull")
          .parameter("field", ApplicationContextSupport.getMessage("solicitudProyectoResponsableEconomico.personaRef"))
          .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProyectoResponsableEconomico.class))
          .build());

      if (solicitudProyecto.getDuracion() != null && ((responsableEconomico.getMesInicio() != null
          && responsableEconomico.getMesInicio() > solicitudProyecto.getDuracion())
          || (responsableEconomico.getMesFin() != null
              && responsableEconomico.getMesFin() > solicitudProyecto.getDuracion()))) {
        throw new SolicitudProyectoResponsableEconomicoMaxMonthException(solicitudProyecto.getDuracion());
      }

      if (responsableEconomicoAnterior != null && (responsableEconomicoAnterior.getMesFin() == null
          || (responsableEconomicoAnterior.getMesFin() != null && responsableEconomico.getMesInicio() == null)
          || (responsableEconomicoAnterior.getMesFin() != null
              && responsableEconomicoAnterior.getMesFin() >= responsableEconomico.getMesInicio()))) {
        throw new SolicitudProyectoResponsableEconomicoOverlapRangeException();
      }

      Set<ConstraintViolation<SolicitudProyectoResponsableEconomico>> result = validator.validate(responsableEconomico,
          SolicitudProyectoResponsableEconomico.OnActualizar.class);

      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      responsableEconomicoAnterior = responsableEconomico;
    }

    List<SolicitudProyectoResponsableEconomico> returnValue = repository.saveAll(responsablesEconomicos);

    log.debug(
        "updateSolicitudProyectoResponsableEconomicos(List<SolicitudProyectoResponsableEconomico> responsablesEconomicos) - start");
    return returnValue;
  }

  /**
   * Obtiene los {@link SolicitudProyectoResponsableEconomico} para una
   * {@link Solicitud}.
   *
   * @param solicitudId el id de la {@link Solicitud}.
   * @param query       la información del filtro.
   * @param pageable    la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoClasificacion} de la
   *         {@link Solicitud} paginadas.
   */
  public Page<SolicitudProyectoResponsableEconomico> findAllBySolicitud(Long solicitudId, String query,
      Pageable pageable) {
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable pageable) - start");
    Specification<SolicitudProyectoResponsableEconomico> specs = SolicitudProyectoResponsableEconomicoSpecifications
        .bySolicitudId(solicitudId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoResponsableEconomico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllBySolicitud(Long solicitudId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link SolicitudProyectoResponsableEconomico} por su id.
   *
   * @param id el id de la entidad {@link SolicitudProyectoResponsableEconomico}.
   * @return la entidad {@link SolicitudProyectoResponsableEconomico}.
   */
  public SolicitudProyectoResponsableEconomico findById(Long id) {
    log.debug("findById(Long id)  - start");
    final SolicitudProyectoResponsableEconomico returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoResponsableEconomicoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}