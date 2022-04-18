package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.GrupoEspecialInvestigacionNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.crue.hercules.sgi.csp.repository.GrupoEspecialInvestigacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoEspecialInvestigacionSpecifications;
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
 * Service para la gestión de {@link GrupoEspecialInvestigacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoEspecialInvestigacionService {

  private final GrupoEspecialInvestigacionRepository repository;

  /**
   * Guarda la entidad {@link GrupoEspecialInvestigacion}.
   * 
   * @param grupoEspecialInvestigacion la entidad
   *                                   {@link GrupoEspecialInvestigacion} a
   *                                   guardar.
   * @return la entidad {@link GrupoEspecialInvestigacion} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoEspecialInvestigacion create(GrupoEspecialInvestigacion grupoEspecialInvestigacion) {
    log.debug("create(GrupoEspecialInvestigacion grupoEspecialInvestigacion) - start");

    AssertHelper.idIsNull(grupoEspecialInvestigacion.getId(), GrupoEspecialInvestigacion.class);
    GrupoEspecialInvestigacion returnValue = repository.save(grupoEspecialInvestigacion);

    log.debug("create(GrupoEspecialInvestigacion grupoEspecialInvestigacion) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link GrupoEspecialInvestigacion}.
   * 
   * @param grupoEspecialInvestigacion la entidad
   *                                   {@link GrupoEspecialInvestigacion} a
   *                                   guardar.
   * @return la entidad {@link GrupoEspecialInvestigacion} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public GrupoEspecialInvestigacion update(GrupoEspecialInvestigacion grupoEspecialInvestigacion) {
    log.debug("update(GrupoEspecialInvestigacion grupoEspecialInvestigacion) - start");

    AssertHelper.idNotNull(grupoEspecialInvestigacion.getId(), GrupoEspecialInvestigacion.class);
    GrupoEspecialInvestigacion returnValue = repository.save(grupoEspecialInvestigacion);

    log.debug("update(GrupoEspecialInvestigacion grupoEspecialInvestigacion) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link GrupoEspecialInvestigacion} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoEspecialInvestigacion}.
   * @return la entidad {@link GrupoEspecialInvestigacion}.
   */
  public GrupoEspecialInvestigacion findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoEspecialInvestigacion.class);
    final GrupoEspecialInvestigacion returnValue = repository.findById(id)
        .orElseThrow(() -> new GrupoEspecialInvestigacionNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link GrupoEspecialInvestigacion} paginadas y/o
   * filtradas del {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoEspecialInvestigacion} paginadas
   *         y/o filtradas.
   */
  public Page<GrupoEspecialInvestigacion> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    Specification<GrupoEspecialInvestigacion> specs = GrupoEspecialInvestigacionSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoEspecialInvestigacion> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

}
