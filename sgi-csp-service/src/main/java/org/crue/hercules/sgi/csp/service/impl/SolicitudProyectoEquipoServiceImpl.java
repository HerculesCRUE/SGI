package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.MiembroSolicitudProyectoEquipoOverlapRangeException;
import org.crue.hercules.sgi.csp.exceptions.MissingInvestigadorPrincipalInSolicitudProyectoEquipoException;
import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEquipo;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEquipoService;
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
 * Service Implementation para gestion {@link SolicitudProyectoEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SolicitudProyectoEquipoServiceImpl implements SolicitudProyectoEquipoService {

  private final Validator validator;

  private final SolicitudProyectoEquipoRepository repository;

  private final RolProyectoRepository rolProyectoRepository;

  private final SolicitudRepository solicitudRepository;

  public SolicitudProyectoEquipoServiceImpl(Validator validator, SolicitudProyectoEquipoRepository repository,
      RolProyectoRepository rolProyectoRepository, SolicitudRepository solicitudRepository) {
    this.validator = validator;
    this.repository = repository;
    this.rolProyectoRepository = rolProyectoRepository;
    this.solicitudRepository = solicitudRepository;
  }

  /**
   * Obtiene una entidad {@link SolicitudProyectoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoEquipo}.
   * @return SolicitudProyectoEquipo la entidad {@link SolicitudProyectoEquipo}.
   */
  @Override
  public SolicitudProyectoEquipo findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoEquipo returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoEquipoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link SolicitudProyectoEquipo} para un
   * {@link SolicitudProyecto}.
   *
   * @param solicitudProyectoId el id del {@link SolicitudProyecto}.
   * @param query               la información del filtro.
   * @param paging              la información de la paginación.
   * @return la lista de entidades {@link SolicitudProyectoEquipo} del
   *         {@link SolicitudProyecto} paginadas.
   */
  @Override
  public Page<SolicitudProyectoEquipo> findAllBySolicitud(Long solicitudProyectoId, String query, Pageable paging) {
    log.debug("findAllBySolicitudProyecto(Long solicitudProyectoId, String query, Pageable paging) - start");

    Specification<SolicitudProyectoEquipo> specs = SolicitudProyectoEquipoSpecifications
        .bySolicitudId(solicitudProyectoId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoEquipo> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyecto(Long solicitudProyectoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link SolicitudProyectoEquipo} del
   * {@link SolicitudProyecto}, elimina los que no están en la lista actual pero
   * tienen el mismo solicitudProyectoId , añade los que no tienen id y actualiza
   * los ya existentes .
   * 
   * @param solicitudProyectoId Id de {@link SolicitudProyecto}
   * @param solicitudProyectoEquipos la lista con los cambios para aplicar.
   * @return La lista actualizada de {@link SolicitudProyectoEquipo}.
   */
  @Transactional
  @Override
  @Validated({ SolicitudProyectoEquipo.OnCrear.class, SolicitudProyectoEquipo.OnActualizar.class })
  public List<SolicitudProyectoEquipo> updateSolicitudProyectoEquipo(Long solicitudProyectoId,
      List<SolicitudProyectoEquipo> solicitudProyectoEquipos) {

    List<SolicitudProyectoEquipo> solicitudProyectoEquiposBD = repository
        .findAllBySolicitudProyectoId(solicitudProyectoId);

    // SolicitudProyectoEquipo eliminados
    List<SolicitudProyectoEquipo> solicitudProyectoEquipoEliminar = solicitudProyectoEquiposBD.stream()
        .filter(solProyEquip -> !solicitudProyectoEquipos.stream().map(SolicitudProyectoEquipo::getId)
            .anyMatch(id -> id == solProyEquip.getId()))
        .collect(Collectors.toList());

    if (!solicitudProyectoEquipoEliminar.isEmpty()) {
      repository.deleteAll(solicitudProyectoEquipoEliminar);
    }
    if (solicitudProyectoEquipos.isEmpty()) {
      return new ArrayList<>();
    }

    // SolicitudProyectoEquipo NO encontrados
    solicitudProyectoEquipos.stream().forEach(solProyEquip -> {
      if (!solicitudProyectoEquiposBD.stream().map(SolicitudProyectoEquipo::getId)
          .anyMatch(id -> id == solProyEquip.getId()) && solProyEquip.getId() != null) {
        throw new SolicitudProyectoEquipoNotFoundException(solProyEquip.getId());
      }
    });

    Solicitud solicitud = solicitudRepository.findById(solicitudProyectoId)
        .orElseThrow(() -> new SolicitudNotFoundException(solicitudProyectoId));

    validateSolicitudProyectoEquipo(solicitud, solicitudProyectoEquipos);

    List<SolicitudProyectoEquipo> returnValue = repository.saveAll(solicitudProyectoEquipos);

    return returnValue;
  }

  private void validateSolicitudProyectoEquipo(Solicitud solicitud,
      List<SolicitudProyectoEquipo> solicitudProyectoEquipos) {

    solicitudProyectoEquipos.sort(
        Comparator.comparing(SolicitudProyectoEquipo::getMesInicio, Comparator.nullsFirst(Comparator.naturalOrder())));

    boolean hasSolicitanteInvestigadorPrincipal = false;

    List<String> personasRef = solicitudProyectoEquipos.stream().map(SolicitudProyectoEquipo::getPersonaRef).distinct()
        .collect(Collectors.toList());

    for (String personaRef : personasRef) {
      SolicitudProyectoEquipo solicitudProyectoEquipoAnterior = null;

      List<SolicitudProyectoEquipo> miembrosPersonaRef = solicitudProyectoEquipos.stream()
          .filter(solProyecEquip -> solProyecEquip.getPersonaRef().equals(personaRef)).collect(Collectors.toList());

      for (SolicitudProyectoEquipo solicitudProyectoEquipo : miembrosPersonaRef) {
        Assert.notNull(solicitudProyectoEquipo.getPersonaRef(),
            () -> ProblemMessage.builder().key(Assert.class, "notNull")
                .parameter("field", ApplicationContextSupport.getMessage("solicitudProyectoEquipo.personaRef"))
                .parameter("entity", ApplicationContextSupport.getMessage(SolicitudProyectoEquipo.class)).build());

        if (solicitudProyectoEquipo.getPersonaRef().equals(solicitud.getSolicitanteRef())) {
          hasSolicitanteInvestigadorPrincipal = true;
        }

        if ((solicitudProyectoEquipoAnterior != null) && (solicitudProyectoEquipoAnterior.getMesFin() != null
            && solicitudProyectoEquipoAnterior.getMesFin() >= solicitudProyectoEquipo.getMesInicio())) {
          throw new MiembroSolicitudProyectoEquipoOverlapRangeException();
        }

        if (solicitudProyectoEquipo.getRolProyecto() == null
            && solicitudProyectoEquipo.getRolProyecto().getId() == null) {
          Set<ConstraintViolation<SolicitudProyectoEquipo>> result = validator.validate(solicitudProyectoEquipo,
              SolicitudProyectoEquipo.OnActualizarRolProyectoSolicitudProyectoEquipo.class);

          if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
          }
        }

        if (!rolProyectoRepository.existsById(solicitudProyectoEquipo.getRolProyecto().getId())) {
          throw new RolProyectoNotFoundException(solicitudProyectoEquipo.getRolProyecto().getId());
        }

        solicitudProyectoEquipoAnterior = solicitudProyectoEquipo;
      }

    }
    if (!hasSolicitanteInvestigadorPrincipal) {
      throw new MissingInvestigadorPrincipalInSolicitudProyectoEquipoException();
    }

  }

}