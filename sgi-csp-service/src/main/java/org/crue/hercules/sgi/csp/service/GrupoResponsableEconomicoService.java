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
import org.crue.hercules.sgi.csp.exceptions.GrupoResponsableEconomicoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoResponsableEconomicoOverlapRangeException;
import org.crue.hercules.sgi.csp.exceptions.GrupoResponsableEconomicoProjectRangeException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.GrupoResponsableEconomicoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoResponsableEconomicoSpecifications;
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
 * Service para la gestión de {@link GrupoResponsableEconomico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoResponsableEconomicoService {

  private final GrupoResponsableEconomicoRepository repository;
  private final GrupoRepository grupoRepository;
  private final Validator validator;
  private final GrupoAuthorityHelper authorityHelper;

  /**
   * Obtiene una entidad {@link GrupoResponsableEconomico} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoResponsableEconomico}.
   * @return la entidad {@link GrupoResponsableEconomico}.
   */
  public GrupoResponsableEconomico findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoResponsableEconomico.class);
    final GrupoResponsableEconomico returnValue = repository.findById(id)
        .orElseThrow(() -> new GrupoResponsableEconomicoNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewGrupo(returnValue.getGrupoId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link GrupoResponsableEconomico} paginadas y/o
   * filtradas del
   * {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoResponsableEconomico} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoResponsableEconomico> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoResponsableEconomico> specs = GrupoResponsableEconomicoSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoResponsableEconomico> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoResponsableEconomico} de la {@link Grupo}
   * con el
   * listado grupoResponsableEconomicos añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   *
   * @param grupoId                    Id de la {@link Grupo}.
   * @param grupoResponsableEconomicos lista con los nuevos
   *                                   {@link GrupoResponsableEconomico} a
   *                                   guardar.
   * @return la entidad {@link GrupoResponsableEconomico} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<GrupoResponsableEconomico> update(Long grupoId,
      @Valid List<GrupoResponsableEconomico> grupoResponsableEconomicos) {
    log.debug("update(Long grupoId, List<GrupoResponsableEconomico> grupoResponsableEconomicos) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Grupo grupo = grupoRepository.findById(grupoId)
        .orElseThrow(() -> new GrupoNotFoundException(grupoId));

    List<GrupoResponsableEconomico> grupoResponsableEconomicosBD = repository.findAllByGrupoId(grupoId);

    // Miembros del responsableEconomico eliminados
    List<GrupoResponsableEconomico> grupoResponsableEconomicosEliminar = grupoResponsableEconomicosBD.stream()
        .filter(grupoResponsableEconomico -> grupoResponsableEconomicos.stream().map(GrupoResponsableEconomico::getId)
            .noneMatch(id -> Objects.equals(id, grupoResponsableEconomico.getId())))
        .collect(Collectors.toList());

    if (!grupoResponsableEconomicosEliminar.isEmpty()) {
      repository.deleteAll(grupoResponsableEconomicosEliminar);
    }

    this.validateGrupoResponsableEconomico(grupoResponsableEconomicos, grupo);

    List<GrupoResponsableEconomico> returnValue = repository.saveAll(grupoResponsableEconomicos);
    log.debug("update(Long grupoId, List<GrupoResponsableEconomico> grupoResponsableEconomicos) - END");

    return returnValue;
  }

  private void validateGrupoResponsableEconomico(List<GrupoResponsableEconomico> grupoResponsableEconomicos,
      Grupo grupo) {

    // Ordena los responsables por fechaInicial
    grupoResponsableEconomicos.sort(Comparator.comparing(GrupoResponsableEconomico::getFechaInicio));

    Instant lastEnd = null;
    boolean emptyFechaInicio = false;
    boolean emptyFechaFin = false;
    for (GrupoResponsableEconomico responsableEconomico : grupoResponsableEconomicos) {
      Instant fechaFinGrupo = grupo.getFechaFin();

      if (emptyFechaInicio && responsableEconomico.getFechaInicio() == null) {
        // Solo puede haber un registro con la fecha de inicio vacia
        throw new GrupoResponsableEconomicoOverlapRangeException();
      }
      if (emptyFechaFin && responsableEconomico.getFechaFin() == null) {
        // Solo puede haber un registro con la fecha de fin vacia
        throw new GrupoResponsableEconomicoOverlapRangeException();
      }
      if (!emptyFechaInicio && responsableEconomico.getFechaInicio() == null) {
        emptyFechaInicio = true;
      }
      if (!emptyFechaFin && responsableEconomico.getFechaFin() == null) {
        emptyFechaFin = true;
      }

      if ((responsableEconomico.getFechaInicio() != null
          && responsableEconomico.getFechaInicio().isBefore(grupo.getFechaInicio()))
          || (responsableEconomico.getFechaFin() != null && fechaFinGrupo != null
              && responsableEconomico.getFechaFin().isAfter(fechaFinGrupo))) {
        throw new GrupoResponsableEconomicoProjectRangeException(responsableEconomico.getFechaInicio(),
            fechaFinGrupo);
      }

      if (lastEnd != null && responsableEconomico.getFechaInicio() != null
          && responsableEconomico.getFechaInicio().isBefore(lastEnd)) {
        // La fecha de inicio no puede ser anterior a la fecha fin del anterior elemento
        throw new GrupoResponsableEconomicoOverlapRangeException();
      }

      Set<ConstraintViolation<GrupoResponsableEconomico>> result = validator.validate(responsableEconomico,
          BaseEntity.Update.class);

      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      lastEnd = responsableEconomico.getFechaFin() != null ? responsableEconomico.getFechaFin() : fechaFinGrupo;
    }
  }

}
