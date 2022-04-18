package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoDto;
import org.crue.hercules.sgi.csp.exceptions.GrupoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoEquipoUniqueException;
import org.crue.hercules.sgi.csp.exceptions.GrupoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.RolProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.csp.repository.GrupoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.RolProyectoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoEquipoSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.PeriodDateUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link GrupoEquipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoEquipoService {

  private final SgiConfigProperties sgiConfigProperties;
  private final GrupoEquipoRepository repository;
  private final GrupoRepository grupoRepository;
  private final Validator validator;
  private final RolProyectoRepository rolProyectoRepository;

  /**
   * Guarda la entidad {@link GrupoEquipo}.
   * 
   * @param grupoEquipo la entidad {@link GrupoEquipo} a guardar.
   * @return la entidad {@link GrupoEquipo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoEquipo create(@Valid GrupoEquipo grupoEquipo) {
    log.debug("create(GrupoEquipo grupoEquipo) - start");

    AssertHelper.idIsNull(grupoEquipo.getId(), GrupoEquipo.class);
    GrupoEquipo returnValue = repository.save(grupoEquipo);

    log.debug("create(GrupoEquipo grupoEquipo) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link GrupoEquipo}.
   *
   * @param grupoEquipoActualizar {@link GrupoEquipo} con los datos actualizados.
   * @return {@link GrupoEquipo} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public GrupoEquipo update(@Valid GrupoEquipo grupoEquipoActualizar) {
    log.debug("update(GrupoEquipo grupoEquipoActualizar) - start");

    AssertHelper.idNotNull(grupoEquipoActualizar.getId(), GrupoEquipo.class);

    return repository.findById(grupoEquipoActualizar.getId()).map(data -> {
      data.setFechaInicio(grupoEquipoActualizar.getFechaInicio());
      data.setFechaFin(grupoEquipoActualizar.getFechaFin());
      data.setPersonaRef(grupoEquipoActualizar.getPersonaRef());
      data.setDedicacion(grupoEquipoActualizar.getDedicacion());
      data.setParticipacion(grupoEquipoActualizar.getParticipacion());

      GrupoEquipo returnValue = repository.save(data);

      log.debug("update(GrupoEquipo grupoEquipoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoEquipoNotFoundException(grupoEquipoActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link GrupoEquipo} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoEquipo}.
   * @return la entidad {@link GrupoEquipo}.
   */
  public GrupoEquipo findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoEquipo.class);
    final GrupoEquipo returnValue = repository.findById(id).orElseThrow(() -> new GrupoEquipoNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link GrupoEquipo}.
   *
   * @param id Id del {@link GrupoEquipo}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, GrupoEquipo.class);

    if (!repository.existsById(id)) {
      throw new GrupoEquipoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades {@link GrupoEquipo} paginadas y/o filtradas del
   * {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoEquipo} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoEquipo> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    Specification<GrupoEquipo> specs = GrupoEquipoSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoEquipo> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista filtrada de investigadores principales del
   * {@link Grupo} en el momento actual con mayor porcentaje de particitacion.
   *
   * Son investiador principales los {@link GrupoEquipo} que a fecha actual
   * tiene el rol con el flag RolGrupo#rolPrincipal a
   * <code>true</code>. En caso de existir mas de un {@link GrupoEquipo}, se
   * recupera el que tenga el mayor porcentaje de dedicación al grupo
   * ({@link GrupoEquipo#participacion}) y en caso de que varios tengan la misma
   * participacion se devuelven todos los que coincidan.
   * 
   * @param grupoId Identificador del {@link Grupo}.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  public List<String> findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(Long grupoId) {
    log.debug("findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(Long grupoId) - start");

    AssertHelper.idNotNull(grupoId, Grupo.class);
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();
    List<String> returnValue = repository.findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(grupoId,
        fechaActual);

    log.debug("findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(Long grupoId) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista filtrada de investigadores principales del {@link Grupo}
   * en el momento actual.
   *
   * Son investiador principales los {@link GrupoEquipo} que a fecha actual
   * tiene el rol con el flag RolGrupo#rolPrincipal a
   * <code>true</code>.
   * 
   * @param grupoId Identificador del {@link Grupo}.
   * @return la lista de personaRef de los investigadores principales del
   *         {@link Grupo} en el momento actual.
   */
  public List<String> findPersonaRefInvestigadoresPrincipales(Long grupoId) {
    log.debug("findPersonaRefInvestigadoresPrincipales(Long grupoId) - start");

    AssertHelper.idNotNull(grupoId, Grupo.class);
    Instant fechaActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).toInstant();
    List<String> returnValue = repository.findPersonaRefInvestigadoresPrincipales(grupoId, fechaActual);

    log.debug("findPersonaRefInvestigadoresPrincipales(Long grupoId) - end");
    return returnValue;
  }

  /**
   * Comprueba si personaRef pertenece a un grupo de investigación con un rol con
   * el flag de baremable a true a fecha 31 de diciembre del año que se esta
   * baremando y el grupo al que pertenecen los autores (tabla Grupo) este activo
   * y el campo "Grupo especial de investigación" a "No" el 31 de diciembre del
   * año que se esta baremando
   *
   * @param personaRef personaRef
   * @param anio       año de baremación
   * @return true/false
   */
  public boolean isPersonaBaremable(String personaRef, Integer anio) {

    Instant fechaBaremacion = PeriodDateUtil.calculateFechaFinBaremacionByAnio(anio, sgiConfigProperties.getTimeZone());
    return repository.isPersonaBaremable(personaRef, fechaBaremacion);
  }

  /**
   * Devuelve una lista de {@link GrupoEquipoDto} pertenecientes a un determinado
   * grupo y que estén a 31 de diciembre del año de baremación
   *
   * @param grupoRef grupoRef
   * @param anio     año de baremación
   * @return Lista de {@link GrupoEquipoDto}
   */
  public List<GrupoEquipoDto> findByGrupoIdAndAnio(Long grupoRef, Integer anio) {

    Instant fechaBaremacion = PeriodDateUtil.calculateFechaFinBaremacionByAnio(anio, sgiConfigProperties.getTimeZone());
    return repository.findByGrupoIdAndAnio(grupoRef, fechaBaremacion);
  }

  /**
   * Actualiza el listado de {@link GrupoEquipo} de la {@link Grupo} con el
   * listado grupoEquipos añadiendo, editando o eliminando los elementos segun
   * proceda.
   *
   * @param grupoId      Id de la {@link Grupo}.
   * @param grupoEquipos lista con los nuevos {@link GrupoEquipo} a guardar.
   * @return la entidad {@link GrupoEquipo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<GrupoEquipo> update(Long grupoId, @Valid List<GrupoEquipo> grupoEquipos) {
    log.debug("update(Long grupoId, List<GrupoEquipo> grupoEquipos) - start");

    grupoRepository.findById(grupoId)
        .orElseThrow(() -> new GrupoNotFoundException(grupoId));

    List<GrupoEquipo> grupoEquiposBD = repository.findAllByGrupoId(grupoId);

    // Miembros del equipo eliminados
    List<GrupoEquipo> grupoEquiposEliminar = grupoEquiposBD.stream()
        .filter(grupoEquipo -> grupoEquipos.stream().map(GrupoEquipo::getId)
            .noneMatch(id -> Objects.equals(id, grupoEquipo.getId())))
        .collect(Collectors.toList());

    if (!grupoEquiposEliminar.isEmpty()) {
      grupoEquiposEliminar.forEach(grupoEquipoEliminar -> {
        Set<ConstraintViolation<GrupoEquipo>> resultValidateEliminar = validator.validate(
            grupoEquipoEliminar,
            GrupoEquipo.OnDelete.class);
        if (!resultValidateEliminar.isEmpty()) {
          throw new ConstraintViolationException(resultValidateEliminar);
        }
      });

      repository.deleteAll(grupoEquiposEliminar);
    }

    this.validateGrupoEquipo(grupoEquipos);

    List<GrupoEquipo> returnValue = repository.saveAll(grupoEquipos);
    log.debug("update(Long grupoId, List<GrupoEquipo> grupoEquipos) - END");

    return returnValue;
  }

  private void validateGrupoEquipo(List<GrupoEquipo> grupoEquipos) {

    grupoEquipos.sort(
        Comparator.comparing(GrupoEquipo::getFechaInicio, Comparator.nullsFirst(Comparator.naturalOrder())));

    List<String> personasRef = grupoEquipos.stream().map(GrupoEquipo::getPersonaRef).distinct()
        .collect(Collectors.toList());

    for (String personaRef : personasRef) {
      GrupoEquipo grupoEquipoAnterior = null;

      List<GrupoEquipo> miembrosPersonaRef = grupoEquipos.stream()
          .filter(solProyecEquip -> solProyecEquip.getPersonaRef().equals(personaRef)).collect(Collectors.toList());

      for (GrupoEquipo grupoEquipo : miembrosPersonaRef) {
        Assert.notNull(grupoEquipo.getPersonaRef(),
            () -> ProblemMessage.builder().key(Assert.class, "notNull")
                .parameter("field", ApplicationContextSupport.getMessage("grupoEquipo.personaRef"))
                .parameter("entity", ApplicationContextSupport.getMessage(GrupoEquipo.class)).build());

        if (grupoEquipoAnterior != null
            && grupoEquipoAnterior.getPersonaRef().equals(grupoEquipo.getPersonaRef())
            && !(grupoEquipoAnterior.getFechaFin() != null
                && grupoEquipoAnterior.getFechaFin().isBefore(grupoEquipo.getFechaInicio()))) {
          throw new GrupoEquipoUniqueException();
        }

        if (grupoEquipo.getRol() == null
            && grupoEquipo.getRol().getId() == null) {
          Set<ConstraintViolation<GrupoEquipo>> result = validator.validate(grupoEquipo,
              BaseEntity.Update.class);

          if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
          }
        }

        if (!rolProyectoRepository.existsById(grupoEquipo.getRol().getId())) {
          throw new RolProyectoNotFoundException(grupoEquipo.getRol().getId());
        }

        grupoEquipoAnterior = grupoEquipo;
      }

    }
  }

}
