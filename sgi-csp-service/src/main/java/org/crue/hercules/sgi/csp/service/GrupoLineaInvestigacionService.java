package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.GrupoLineaInvestigacionNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoLineaInvestigacionProjectRangeException;
import org.crue.hercules.sgi.csp.exceptions.GrupoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.repository.GrupoLineaClasificacionRepository;
import org.crue.hercules.sgi.csp.repository.GrupoLineaEquipoInstrumentalRepository;
import org.crue.hercules.sgi.csp.repository.GrupoLineaInvestigacionRepository;
import org.crue.hercules.sgi.csp.repository.GrupoLineaInvestigadorRepository;
import org.crue.hercules.sgi.csp.repository.GrupoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoLineaInvestigacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.GrupoLineaInvestigacionAuthorityHelper;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link GrupoLineaInvestigacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoLineaInvestigacionService {

  private final GrupoLineaInvestigacionRepository repository;
  private final GrupoRepository grupoRepository;
  private final Validator validator;
  private final GrupoLineaInvestigacionAuthorityHelper authorityHelper;
  private final GrupoLineaInvestigadorRepository grupoLineaInvestigadorRepository;
  private final GrupoLineaClasificacionRepository grupoLineaClasificacionRepository;
  private final GrupoLineaEquipoInstrumentalRepository grupoLineaEquipoInstrumentalRepository;

  /**
   * Guarda la entidad {@link GrupoLineaInvestigacion}.
   * 
   * @param grupoLineaInvestigacion la entidad {@link GrupoLineaInvestigacion}
   *                                a guardar.
   * @return la entidad {@link GrupoLineaInvestigacion} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoLineaInvestigacion create(@Valid GrupoLineaInvestigacion grupoLineaInvestigacion) {
    log.debug("create(GrupoLineaInvestigacion grupoLineaInvestigacion) - start");

    authorityHelper.checkUserHasAuthorityViewGrupo(grupoLineaInvestigacion.getGrupoId());

    AssertHelper.idIsNull(grupoLineaInvestigacion.getId(), GrupoLineaInvestigacion.class);
    GrupoLineaInvestigacion returnValue = repository.save(grupoLineaInvestigacion);

    log.debug("create(GrupoLineaInvestigacion grupoLineaInvestigacion) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link GrupoLineaInvestigacion}.
   *
   * @param grupoLineaInvestigacionActualizar {@link GrupoLineaInvestigacion}
   *                                          con los datos actualizados.
   * @return {@link GrupoLineaInvestigacion} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public GrupoLineaInvestigacion update(@Valid GrupoLineaInvestigacion grupoLineaInvestigacionActualizar) {
    log.debug("update(GrupoLineaInvestigacion grupoLineaInvestigacionActualizar) - start");

    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(grupoLineaInvestigacionActualizar.getId());

    validateGrupoLineaInvestigacion(grupoLineaInvestigacionActualizar);

    return repository.findById(grupoLineaInvestigacionActualizar.getId()).map(data -> {
      data.setFechaInicio(grupoLineaInvestigacionActualizar.getFechaInicio());
      data.setFechaFin(grupoLineaInvestigacionActualizar.getFechaFin());
      data.setLineaInvestigacionId(grupoLineaInvestigacionActualizar.getLineaInvestigacionId());

      GrupoLineaInvestigacion returnValue = repository.save(data);

      log.debug("update(GrupoLineaInvestigacion grupoLineaInvestigacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoLineaInvestigacionNotFoundException(grupoLineaInvestigacionActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link GrupoLineaInvestigacion} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoLineaInvestigacion}.
   * @return la entidad {@link GrupoLineaInvestigacion}.
   */
  public GrupoLineaInvestigacion findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoLineaInvestigacion.class);
    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(id);

    final GrupoLineaInvestigacion returnValue = repository.findById(id)
        .orElseThrow(() -> new GrupoLineaInvestigacionNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link GrupoLineaInvestigacion}.
   *
   * @param id Id del {@link GrupoLineaInvestigacion}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, GrupoLineaInvestigacion.class);
    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(id);

    if (!repository.existsById(id)) {
      throw new GrupoLineaInvestigacionNotFoundException(id);
    }

    // Se eliminan los registros en las entidades relacionadas
    grupoLineaInvestigadorRepository.deleteAllByGrupoLineaInvestigacionId(id);
    grupoLineaClasificacionRepository.deleteAllByGrupoLineaInvestigacionId(id);
    grupoLineaEquipoInstrumentalRepository.deleteAllByGrupoLineaInvestigacionId(id);

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades {@link GrupoLineaInvestigacion} paginadas y/o
   * filtradas del
   * {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoLineaInvestigacion} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoLineaInvestigacion> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoLineaInvestigacion> specs = GrupoLineaInvestigacionSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoLineaInvestigacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

  private void validateGrupoLineaInvestigacion(GrupoLineaInvestigacion lineaInvestigacion) {

    Assert.notNull(lineaInvestigacion.getGrupoId(),
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(Grupo.class)).build());

    Assert.notNull(lineaInvestigacion.getLineaInvestigacionId(),
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("grupoLineaInvestigacion.lineaInvestigacion"))
            .parameter("entity", ApplicationContextSupport.getMessage(GrupoLineaInvestigacion.class)).build());

    Grupo grupo = grupoRepository.findById(lineaInvestigacion.getGrupoId())
        .orElseThrow(() -> new GrupoNotFoundException(lineaInvestigacion.getGrupoId()));

    Instant fechaFinGrupo = grupo.getFechaFin();

    if ((lineaInvestigacion.getFechaInicio() != null
        && lineaInvestigacion.getFechaInicio().isBefore(grupo.getFechaInicio()))
        || (lineaInvestigacion.getFechaFin() != null && fechaFinGrupo != null
            && lineaInvestigacion.getFechaFin().isAfter(fechaFinGrupo))) {
      throw new GrupoLineaInvestigacionProjectRangeException(lineaInvestigacion.getFechaInicio(),
          fechaFinGrupo);
    }

    Set<ConstraintViolation<GrupoLineaInvestigacion>> result = validator.validate(lineaInvestigacion,
        BaseEntity.Update.class);

    if (!result.isEmpty()) {
      throw new ConstraintViolationException(result);
    }
  }

  /**
   * Comprueba la existencia del {@link GrupoLineaInvestigacion} por id.
   *
   * @param id el id de la entidad {@link GrupoLineaInvestigacion}.
   * @return <code>true</code> si existe y <code>false</code> en caso contrario.
   */
  public boolean existsById(Long id) {
    log.debug("existsById(Long id)  - start");

    AssertHelper.idNotNull(id, GrupoLineaInvestigacion.class);
    final boolean exists = repository.existsById(id);

    log.debug("existsById(Long id)  - end");
    return exists;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si el
   * {@link GrupoLineaInvestigacion} puede ser modificado. También se utilizará
   * para permitir la creación, modificación o eliminación de ciertas entidades
   * relacionadas con el {@link GrupoLineaInvestigacion}.
   * 
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  public boolean modificable() {
    return authorityHelper.hasAuthorityEditUnidadGestion();
  }

}
