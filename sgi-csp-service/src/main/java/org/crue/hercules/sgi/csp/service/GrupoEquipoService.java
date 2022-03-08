package org.crue.hercules.sgi.csp.service;

import java.time.Instant;
import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.exceptions.GrupoEquipoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.repository.GrupoEquipoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoEquipoSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
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

  /**
   * Guarda la entidad {@link GrupoEquipo}.
   * 
   * @param grupoEquipo la entidad {@link GrupoEquipo} a guardar.
   * @return la entidad {@link GrupoEquipo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoEquipo create(GrupoEquipo grupoEquipo) {
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
   * Obtiene los personaRef del investigador o investigadores principales del
   * {@link Grupo} en el momento actual.
   * 
   * Se considera investiador principal al {@link GrupoEquipo} que a fecha actual
   * tiene el {@link RolProyecto} con el flag "principal" a
   * <code>true</code>. En caso de existir mas de un {@link GrupoEquipo}, se
   * recupera el que tenga el mayor porcentaje de dedicación al grupo (campo
   * "participación").
   * Y en caso de que varios coincidan se devuelven todos los que coincidan.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
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

}
