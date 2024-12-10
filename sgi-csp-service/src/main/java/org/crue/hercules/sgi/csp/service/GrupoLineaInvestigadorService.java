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

import org.crue.hercules.sgi.csp.exceptions.GrupoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoLineaInvestigacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoLineaInvestigadorNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoLineaInvestigadorOverlapRangeException;
import org.crue.hercules.sgi.csp.exceptions.GrupoLineaInvestigadorProjectRangeException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.crue.hercules.sgi.csp.repository.GrupoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.GrupoLineaInvestigacionRepository;
import org.crue.hercules.sgi.csp.repository.GrupoLineaInvestigadorRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoLineaInvestigadorSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.GrupoLineaInvestigacionAuthorityHelper;
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
 * Service para la gestión de {@link GrupoLineaInvestigador}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoLineaInvestigadorService {

  private final GrupoLineaInvestigadorRepository repository;
  private final GrupoLineaInvestigacionRepository grupoLineaInvestigacionRepository;
  private final GrupoEquipoRepository grupoEquipoRepository;
  private final Validator validator;
  private final GrupoLineaInvestigacionAuthorityHelper authorityHelper;

  /**
   * Obtiene una entidad {@link GrupoLineaInvestigador} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoLineaInvestigador}.
   * @return la entidad {@link GrupoLineaInvestigador}.
   */
  public GrupoLineaInvestigador findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoLineaInvestigador.class);
    final GrupoLineaInvestigador returnValue = repository.findById(id)
        .orElseThrow(() -> new GrupoLineaInvestigadorNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(returnValue.getGrupoLineaInvestigacionId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link GrupoLineaInvestigador} paginadas y/o
   * filtradas del {@link GrupoLineaInvestigacion}.
   *
   * @param grupoLineaInvestigacionId Identificador de la entidad
   *                                  {@link GrupoLineaInvestigacion}.
   * @param paging                    la información de la paginación.
   * @param query                     la información del filtro.
   * @return la lista de entidades {@link GrupoLineaInvestigador} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoLineaInvestigador> findAllByGrupoLineaInvestigacion(Long grupoLineaInvestigacionId, String query,
      Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoLineaInvestigacionId, GrupoLineaInvestigacion.class);
    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(grupoLineaInvestigacionId);

    Specification<GrupoLineaInvestigador> specs = GrupoLineaInvestigadorSpecifications.byGrupoLineaInvestigacionId(
        grupoLineaInvestigacionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoLineaInvestigador> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link GrupoLineaInvestigador} paginadas y/o
   * filtradas
   *
   * @param paging la información de la paginación.
   * @param query  la información del filtro.
   * @return la lista de entidades {@link GrupoLineaInvestigador} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoLineaInvestigador> findAll(String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    Specification<GrupoLineaInvestigador> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<GrupoLineaInvestigador> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoLineaInvestigador} de la
   * {@link GrupoLineaInvestigacion}
   * con el
   * listado grupoLineasInvestigadores añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   *
   * @param grupoLineaInvestigacionId Id de la {@link GrupoLineaInvestigacion}.
   * @param grupoLineasInvestigadores lista con los nuevos
   *                                  {@link GrupoLineaInvestigador} a
   *                                  guardar.
   * @return la entidad {@link GrupoLineaInvestigador} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<GrupoLineaInvestigador> update(Long grupoLineaInvestigacionId,
      @Valid List<GrupoLineaInvestigador> grupoLineasInvestigadores) {
    log.debug("update(Long grupoLineaInvestigacionId, List<GrupoLineaInvestigador> grupoLineasInvestigadores) - start");

    AssertHelper.idNotNull(grupoLineaInvestigacionId, GrupoLineaInvestigacion.class);
    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(grupoLineaInvestigacionId);

    GrupoLineaInvestigacion grupoLineaInvestigacion = grupoLineaInvestigacionRepository.findById(
        grupoLineaInvestigacionId)
        .orElseThrow(() -> new GrupoLineaInvestigacionNotFoundException(grupoLineaInvestigacionId));

    List<GrupoLineaInvestigador> grupoLineasInvestigadoresBD = repository.findAllByGrupoLineaInvestigacionId(
        grupoLineaInvestigacionId);

    // Miembros del lineaInvestigador eliminados
    List<GrupoLineaInvestigador> grupoLineasInvestigadoresEliminar = grupoLineasInvestigadoresBD.stream()
        .filter(grupoLineaInvestigador -> grupoLineasInvestigadores.stream().map(GrupoLineaInvestigador::getId)
            .noneMatch(id -> Objects.equals(id, grupoLineaInvestigador.getId())))
        .collect(Collectors.toList());

    if (!grupoLineasInvestigadoresEliminar.isEmpty()) {
      repository.deleteAll(grupoLineasInvestigadoresEliminar);
    }

    this.validateGrupoLineaInvestigador(grupoLineasInvestigadores, grupoLineaInvestigacion);

    List<GrupoLineaInvestigador> returnValue = repository.saveAll(grupoLineasInvestigadores);
    log.debug("update(Long grupoLineaInvestigacionId, List<GrupoLineaInvestigador> grupoLineasInvestigadores) - END");

    return returnValue;
  }

  private void validateGrupoLineaInvestigador(List<GrupoLineaInvestigador> grupoLineasInvestigadores,
      GrupoLineaInvestigacion grupoLineaInvestigacion) {

    // Ordena los responsables por fechaInicial
    grupoLineasInvestigadores.sort(Comparator.comparing(GrupoLineaInvestigador::getPersonaRef)
        .thenComparing(Comparator.comparing(GrupoLineaInvestigador::getFechaInicio)));

    Instant lastEnd = null;
    String lastPersonaRef = null;
    boolean emptyFechaInicio = false;
    boolean emptyFechaFin = false;
    for (GrupoLineaInvestigador lineaInvestigador : grupoLineasInvestigadores) {
      Instant fechaFinGrupo = grupoLineaInvestigacion.getFechaFin();

      if (emptyFechaInicio && lineaInvestigador.getFechaInicio() == null) {
        // Solo puede haber un registro con la fecha de inicio vacia
        throw new GrupoLineaInvestigadorOverlapRangeException();
      }
      if (emptyFechaFin && lineaInvestigador.getPersonaRef().equals(lastPersonaRef)
          && lineaInvestigador.getFechaFin() == null) {
        // Solo puede haber un registro con la fecha de fin vacia
        throw new GrupoLineaInvestigadorOverlapRangeException();
      }
      if (lineaInvestigador.getPersonaRef().equals(lastPersonaRef)
          && !emptyFechaInicio && lineaInvestigador.getFechaInicio() == null) {
        emptyFechaInicio = true;
      }

      if (lineaInvestigador.getPersonaRef().equals(lastPersonaRef)
          && ((lineaInvestigador.getFechaInicio() != null
              && lineaInvestigador.getFechaInicio().isBefore(grupoLineaInvestigacion.getFechaInicio()))
              || (lineaInvestigador.getFechaFin() != null && fechaFinGrupo != null
                  && lineaInvestigador.getFechaFin().isAfter(fechaFinGrupo)))) {
        throw new GrupoLineaInvestigadorProjectRangeException(lineaInvestigador.getFechaInicio(),
            fechaFinGrupo);
      }

      if (lineaInvestigador.getPersonaRef().equals(lastPersonaRef)
          && !emptyFechaFin && lineaInvestigador.getFechaFin() == null) {
        emptyFechaFin = true;
      } else if (!lineaInvestigador.getPersonaRef().equals(lastPersonaRef)) {
        emptyFechaFin = false;
      }

      if (lineaInvestigador.getPersonaRef().equals(lastPersonaRef)
          && lastEnd != null && lineaInvestigador.getFechaInicio() != null
          && lineaInvestigador.getFechaInicio().isBefore(lastEnd)) {
        // La fecha de inicio no puede ser anterior a la fecha fin del anterior elemento
        throw new GrupoLineaInvestigadorOverlapRangeException();
      }

      Set<ConstraintViolation<GrupoLineaInvestigador>> result = validator.validate(lineaInvestigador,
          BaseEntity.Update.class);

      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      lastEnd = lineaInvestigador.getFechaFin() != null ? lineaInvestigador.getFechaFin() : fechaFinGrupo;
      lastPersonaRef = lineaInvestigador.getPersonaRef();
    }
  }

  public boolean existsLineaInvestigadorInFechasGrupoEquipo(Long idGrupoEquipo) {
    GrupoEquipo grupoEquipo = grupoEquipoRepository.findById(idGrupoEquipo)
        .orElseThrow(() -> new GrupoEquipoNotFoundException(idGrupoEquipo));
    Specification<GrupoLineaInvestigador> specPersonaRef = GrupoLineaInvestigadorSpecifications.byPersonaRef(
        grupoEquipo.getPersonaRef());
    Specification<GrupoLineaInvestigador> specInFechas = GrupoLineaInvestigadorSpecifications.byRangoFechaSolapados(
        grupoEquipo.getFechaInicio(), grupoEquipo.getFechaFin());
    Specification<GrupoLineaInvestigador> specs = Specification.where(specPersonaRef).and(specInFechas);

    return repository.count(specs) > 0;
  }

}
