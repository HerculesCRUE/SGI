package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.GrupoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoPersonaAutorizadaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoPersonaAutorizadaOverlapRangeException;
import org.crue.hercules.sgi.csp.exceptions.GrupoPersonaAutorizadaProjectRangeException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.crue.hercules.sgi.csp.repository.GrupoPersonaAutorizadaRepository;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoPersonaAutorizadaSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.GrupoAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link GrupoPersonaAutorizada}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoPersonaAutorizadaService {

  private final GrupoPersonaAutorizadaRepository repository;
  private final GrupoRepository grupoRepository;
  private final Validator validator;
  private final GrupoAuthorityHelper authorityHelper;

  /**
   * Obtiene una entidad {@link GrupoPersonaAutorizada} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoPersonaAutorizada}.
   * @return la entidad {@link GrupoPersonaAutorizada}.
   */
  public GrupoPersonaAutorizada findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoPersonaAutorizada.class);
    final GrupoPersonaAutorizada returnValue = repository.findById(id)
        .orElseThrow(() -> new GrupoPersonaAutorizadaNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewGrupo(returnValue.getGrupoId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link GrupoPersonaAutorizada} paginadas y/o
   * filtradas del
   * {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoPersonaAutorizada} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoPersonaAutorizada> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoPersonaAutorizada> specs = GrupoPersonaAutorizadaSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoPersonaAutorizada> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoPersonaAutorizada} de la {@link Grupo}
   * con el
   * listado grupoPersonasAutorizadas añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   *
   * @param grupoId                  Id de la {@link Grupo}.
   * @param grupoPersonasAutorizadas lista con los nuevos
   *                                 {@link GrupoPersonaAutorizada} a
   *                                 guardar.
   * @return la entidad {@link GrupoPersonaAutorizada} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<GrupoPersonaAutorizada> update(Long grupoId,
      @Valid List<GrupoPersonaAutorizada> grupoPersonasAutorizadas) {
    log.debug("update(Long grupoId, List<GrupoPersonaAutorizada> grupoPersonasAutorizadas) - start");

    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Grupo grupo = grupoRepository.findById(grupoId)
        .orElseThrow(() -> new GrupoNotFoundException(grupoId));

    List<GrupoPersonaAutorizada> grupoPersonasAutorizadasBD = repository.findAllByGrupoId(grupoId);

    // Miembros del personaAutorizada eliminados
    List<GrupoPersonaAutorizada> grupoPersonasAutorizadasEliminar = grupoPersonasAutorizadasBD.stream()
        .filter(grupoPersonaAutorizada -> grupoPersonasAutorizadas.stream().map(GrupoPersonaAutorizada::getId)
            .noneMatch(id -> Objects.equals(id, grupoPersonaAutorizada.getId())))
        .collect(Collectors.toList());

    if (!grupoPersonasAutorizadasEliminar.isEmpty()) {
      repository.deleteAll(grupoPersonasAutorizadasEliminar);
    }

    this.validateGrupoPersonaAutorizada(grupoPersonasAutorizadas, grupo);

    List<GrupoPersonaAutorizada> returnValue = repository.saveAll(grupoPersonasAutorizadas);
    log.debug("update(Long grupoId, List<GrupoPersonaAutorizada> grupoPersonasAutorizadas) - END");

    return returnValue;
  }

  private void validateGrupoPersonaAutorizada(List<GrupoPersonaAutorizada> grupoPersonasAutorizadas,
      Grupo grupo) {

    // Ordena los responsables por fechaInicial
    grupoPersonasAutorizadas.sort(Comparator.comparing(GrupoPersonaAutorizada::getFechaInicio));

    Instant lastEnd = null;
    boolean emptyFechaInicio = false;
    boolean emptyFechaFin = false;
    for (GrupoPersonaAutorizada personaAutorizada : grupoPersonasAutorizadas) {
      Instant fechaFinGrupo = grupo.getFechaFin();

      if (emptyFechaInicio && personaAutorizada.getFechaInicio() == null) {
        // Solo puede haber un registro con la fecha de inicio vacia
        throw new GrupoPersonaAutorizadaOverlapRangeException();
      }
      if (emptyFechaFin && personaAutorizada.getFechaFin() == null) {
        // Solo puede haber un registro con la fecha de fin vacia
        throw new GrupoPersonaAutorizadaOverlapRangeException();
      }
      if (!emptyFechaInicio && personaAutorizada.getFechaInicio() == null) {
        emptyFechaInicio = true;
      }
      if (!emptyFechaFin && personaAutorizada.getFechaFin() == null) {
        emptyFechaFin = true;
      }

      if ((personaAutorizada.getFechaInicio() != null
          && personaAutorizada.getFechaInicio().isBefore(grupo.getFechaInicio()))
          || (personaAutorizada.getFechaFin() != null && fechaFinGrupo != null
              && personaAutorizada.getFechaFin().isAfter(fechaFinGrupo))) {
        throw new GrupoPersonaAutorizadaProjectRangeException(personaAutorizada.getFechaInicio(),
            fechaFinGrupo);
      }

      if (lastEnd != null && personaAutorizada.getFechaInicio() != null
          && personaAutorizada.getFechaInicio().isBefore(lastEnd)) {
        // La fecha de inicio no puede ser anterior a la fecha fin del anterior elemento
        throw new GrupoPersonaAutorizadaOverlapRangeException();
      }

      Set<ConstraintViolation<GrupoPersonaAutorizada>> result = validator.validate(personaAutorizada,
          BaseEntity.Update.class);

      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      lastEnd = personaAutorizada.getFechaFin() != null ? personaAutorizada.getFechaFin() : fechaFinGrupo;
    }
  }

}
