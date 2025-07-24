package org.crue.hercules.sgi.csp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.SolicitudProyectoSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioEquipoRepository;
import org.crue.hercules.sgi.csp.repository.SolicitudProyectoSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.SolicitudProyectoSocioEquipoSpecifications;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoSocioEquipoService;
import org.crue.hercules.sgi.csp.service.SolicitudService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.SolicitudAuthorityHelper;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link SolicitudProyectoSocioEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class SolicitudProyectoSocioEquipoServiceImpl implements SolicitudProyectoSocioEquipoService {

  private static final String MSG_KEY_ENTITY = "entity";
  private static final String MSG_KEY_MSG = "msg";
  private static final String MSG_KEY_FIELD = "field";
  private static final String MSG_KEY_ACTION = "action";
  private static final String MSG_FIELD_ACTION_MODIFICAR = "action.modificar";
  private static final String MSG_FIELD_PERSONA_REF = "personaRef";
  private static final String MSG_MODEL_SOLICITUD_PROYECTO_SOCIO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoSocio.message";
  private static final String MSG_MODEL_SOLICITUD_PROYECTO_SOCIO_EQUIPO = "org.crue.hercules.sgi.csp.model.SolicitudProyectoSocioEquipo.message";
  private static final String MSG_ENTITY_MODIFICABLE = "org.springframework.util.Assert.entity.modificable.message";
  private static final String MSG_PROBLEM_ACCION_DENEGADA = "org.springframework.util.Assert.accion.denegada.message";
  private static final String MSG_PROBLEM_DATE_OVERLOAP = "org.springframework.util.Assert.date.overloap.message";

  /** Solicitud service */
  private final SolicitudService solicitudService;

  /** Solicitud proyecto equipo socio repository */
  private final SolicitudProyectoSocioEquipoRepository repository;

  /** Solicitud proyecto socio repository */
  private final SolicitudProyectoSocioRepository solicitudProyectoSocioRepository;

  private final SolicitudAuthorityHelper solicitudAuthorityHelper;

  public SolicitudProyectoSocioEquipoServiceImpl(SolicitudProyectoSocioEquipoRepository repository,
      SolicitudService solicitudService, SolicitudProyectoSocioRepository solicitudProyectoSocioRepository,
      SolicitudAuthorityHelper solicitudAuthorityHelper) {
    this.repository = repository;
    this.solicitudService = solicitudService;
    this.solicitudProyectoSocioRepository = solicitudProyectoSocioRepository;
    this.solicitudAuthorityHelper = solicitudAuthorityHelper;
  }

  /**
   * Actualiza los datos del {@link SolicitudProyectoSocioEquipo}.
   * 
   * @param solicitudProyectoSocioId      Id de la {@link SolicitudProyectoSocio}.
   * @param solicitudProyectoEquipoSocios lista con los nuevos
   *                                      {@link SolicitudProyectoSocioEquipo} a
   *                                      guardar.
   * @return SolicitudProyectoSocioEquipo {@link SolicitudProyectoSocioEquipo}
   *         actualizado.
   */
  @Override
  @Transactional
  public List<SolicitudProyectoSocioEquipo> update(Long solicitudProyectoSocioId,
      List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) {
    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) - start");

    SolicitudProyectoSocio solicitudProyectoSocio = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    solicitudAuthorityHelper
        .checkUserHasAuthorityModifySolicitud(solicitudProyectoSocio.getSolicitudProyectoId());

    Assert.isTrue(solicitudService.modificable(solicitudProyectoSocio.getSolicitudProyectoId()),
        () -> ProblemMessage.builder()
            .key(MSG_ENTITY_MODIFICABLE)
            .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(MSG_MODEL_SOLICITUD_PROYECTO_SOCIO_EQUIPO))
            .parameter(MSG_KEY_MSG, null)
            .build());

    List<SolicitudProyectoSocioEquipo> existentes = repository
        .findAllBySolicitudProyectoSocioId(solicitudProyectoSocioId);

    // Periodos eliminados
    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoEliminar = existentes.stream()
        .filter(periodo -> solicitudProyectoEquipoSocios.stream().map(SolicitudProyectoSocioEquipo::getId)
            .noneMatch(id -> Objects.equals(id, periodo.getId())))
        .collect(Collectors.toList());

    if (!solicitudProyectoEquipoEliminar.isEmpty()) {
      repository.deleteAll(solicitudProyectoEquipoEliminar);
    }

    if (solicitudProyectoEquipoSocios.isEmpty()) {
      return new ArrayList<>();
    }

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoMesInicioNull = solicitudProyectoEquipoSocios.stream()
        .filter(periodo -> periodo.getMesInicio() == null).collect(Collectors.toList());

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoConMesInicio = solicitudProyectoEquipoSocios.stream()
        .filter(periodo -> periodo.getMesInicio() != null).collect(Collectors.toList());

    solicitudProyectoEquipoMesInicioNull.sort(Comparator.comparing(SolicitudProyectoSocioEquipo::getPersonaRef));

    solicitudProyectoEquipoConMesInicio.sort(Comparator.comparing(SolicitudProyectoSocioEquipo::getMesInicio)
        .thenComparing(Comparator.comparing(SolicitudProyectoSocioEquipo::getPersonaRef)));

    List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoAll = new ArrayList<>();
    solicitudProyectoEquipoAll.addAll(solicitudProyectoEquipoMesInicioNull);
    solicitudProyectoEquipoAll.addAll(solicitudProyectoEquipoConMesInicio);

    SolicitudProyectoSocioEquipo solicitudProyectoEquipoSocioAnterior = null;
    for (SolicitudProyectoSocioEquipo solicitudProyectoSocioEquipo : solicitudProyectoEquipoAll) {

      // Si tiene id se valida que exista y que tenga la solicitud proyecto equipo de
      // la que se estan actualizando los periodos
      if (solicitudProyectoSocioEquipo.getId() != null) {
        SolicitudProyectoSocioEquipo existente = existentes.stream()
            .filter(equipoSocio -> Objects.equals(equipoSocio.getId(), solicitudProyectoSocioEquipo.getId()))
            .findFirst()
            .orElseThrow(() -> new SolicitudProyectoSocioEquipoNotFoundException(solicitudProyectoSocioEquipo.getId()));

        Assert.isTrue(
            Objects.equals(existente.getSolicitudProyectoSocioId(),
                solicitudProyectoSocioEquipo.getSolicitudProyectoSocioId()),
            () -> ProblemMessage.builder()
                .key(MSG_PROBLEM_ACCION_DENEGADA)
                .parameter(MSG_KEY_FIELD, ApplicationContextSupport.getMessage(
                    MSG_MODEL_SOLICITUD_PROYECTO_SOCIO))
                .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                    MSG_MODEL_SOLICITUD_PROYECTO_SOCIO_EQUIPO))
                .parameter(MSG_KEY_ACTION, ApplicationContextSupport.getMessage(MSG_FIELD_ACTION_MODIFICAR))
                .build());
      }

      // Setea la solicitud proyecto socio recuperada del solicitudProyectoSocioId
      solicitudProyectoSocioEquipo.setSolicitudProyectoSocioId(solicitudProyectoSocio.getId());

      // Validaciones
      AssertHelper.entityNotNull(solicitudProyectoSocioEquipo.getRolProyecto(), SolicitudProyectoSocioEquipo.class,
          RolProyecto.class);
      AssertHelper.fieldNotNull(solicitudProyectoSocioEquipo.getPersonaRef(), SolicitudProyectoSocioEquipo.class,
          MSG_FIELD_PERSONA_REF);

      Assert.isTrue(
          solicitudProyectoEquipoSocioAnterior == null || (!solicitudProyectoEquipoSocioAnterior.getPersonaRef()
              .equals(solicitudProyectoSocioEquipo.getPersonaRef())
              || (solicitudProyectoEquipoSocioAnterior.getPersonaRef()
                  .equals(solicitudProyectoSocioEquipo.getPersonaRef())
                  && (solicitudProyectoSocioEquipo.getMesInicio() > solicitudProyectoEquipoSocioAnterior
                      .getMesFin()))),
          () -> ProblemMessage.builder()
              .key(MSG_PROBLEM_DATE_OVERLOAP)
              .parameter(MSG_KEY_ENTITY, ApplicationContextSupport.getMessage(
                  MSG_MODEL_SOLICITUD_PROYECTO_SOCIO_EQUIPO))
              .parameter(MSG_KEY_FIELD, solicitudProyectoSocioEquipo.getPersonaRef())
              .build());

      solicitudProyectoEquipoSocioAnterior = solicitudProyectoSocioEquipo;
    }

    List<SolicitudProyectoSocioEquipo> returnValue = repository.saveAll(solicitudProyectoEquipoSocios);
    log.debug(
        "update(Long solicitudProyectoSocioId,  List<SolicitudProyectoSocioEquipo> solicitudProyectoEquipoSocios) - end");

    return returnValue;
  }

  /**
   * Obtiene una entidad {@link SolicitudProyectoSocioEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link SolicitudProyectoSocioEquipo}.
   * @return SolicitudProyectoSocioEquipo la entidad
   *         {@link SolicitudProyectoSocioEquipo}.
   */
  @Override
  public SolicitudProyectoSocioEquipo findById(Long id) {
    log.debug("findById(Long id) - start");
    final SolicitudProyectoSocioEquipo returnValue = repository.findById(id)
        .orElseThrow(() -> new SolicitudProyectoSocioEquipoNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene la {@link SolicitudProyectoSocioEquipo} para una
   * {@link SolicitudProyectoSocio}.
   *
   * @param solicitudProyectoSocioId el id de la {@link SolicitudProyectoSocio}.
   * @return la lista de entidades {@link SolicitudProyectoSocioEquipo} de la
   *         {@link SolicitudProyectoSocio} paginadas.
   */
  @Override
  public Page<SolicitudProyectoSocioEquipo> findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query,
      Pageable paging) {
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - start");

    SolicitudProyectoSocio solicitudProyectoSocio = solicitudProyectoSocioRepository.findById(solicitudProyectoSocioId)
        .orElseThrow(() -> new SolicitudProyectoSocioNotFoundException(solicitudProyectoSocioId));

    solicitudAuthorityHelper.checkUserHasAuthorityViewSolicitud(solicitudProyectoSocio.getSolicitudProyectoId());

    Specification<SolicitudProyectoSocioEquipo> specs = SolicitudProyectoSocioEquipoSpecifications
        .bySolicitudProyectoSocio(solicitudProyectoSocioId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<SolicitudProyectoSocioEquipo> returnValue = repository.findAll(specs, paging);
    log.debug("findAllBySolicitudProyectoSocio(Long solicitudProyectoSocioId, String query, Pageable paging) - end");
    return returnValue;

  }

}