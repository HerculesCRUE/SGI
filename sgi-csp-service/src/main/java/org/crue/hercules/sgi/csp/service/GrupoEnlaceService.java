package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GrupoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEnlace;
import org.crue.hercules.sgi.csp.repository.GrupoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoEnlaceSpecifications;
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
 * Service para la gestión de {@link GrupoEnlace}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoEnlaceService {

  private final GrupoEnlaceRepository repository;
  private final GrupoAuthorityHelper authorityHelper;

  /**
   * Guarda la entidad {@link GrupoEnlace}.
   * 
   * @param grupoEnlace la entidad {@link GrupoEnlace} a guardar.
   * @return la entidad {@link GrupoEnlace} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoEnlace create(@Valid GrupoEnlace grupoEnlace) {
    log.debug("create(GrupoEnlace grupoEnlace) - start");

    AssertHelper.idIsNull(grupoEnlace.getId(), GrupoEnlace.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoEnlace.getGrupoId());

    GrupoEnlace returnValue = repository.save(grupoEnlace);

    log.debug("create(GrupoEnlace grupoEnlace) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link GrupoEnlace}.
   *
   * @param grupoEnlaceActualizar {@link GrupoEnlace} con los datos
   *                              actualizados.
   * @return {@link GrupoEnlace} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public GrupoEnlace update(@Valid GrupoEnlace grupoEnlaceActualizar) {
    log.debug("update(GrupoEnlace grupoEnlaceActualizar) - start");

    AssertHelper.idNotNull(grupoEnlaceActualizar.getId(), GrupoEnlace.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoEnlaceActualizar.getGrupoId());

    return repository.findById(grupoEnlaceActualizar.getId()).map(data -> {
      data.setEnlace(grupoEnlaceActualizar.getEnlace());

      GrupoEnlace returnValue = repository.save(data);

      log.debug("update(GrupoEnlace grupoEnlaceActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoEnlaceNotFoundException(grupoEnlaceActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link GrupoEnlace} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoEnlace}.
   * @return la entidad {@link GrupoEnlace}.
   */
  public GrupoEnlace findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoEnlace.class);
    final GrupoEnlace returnValue = repository.findById(id)
        .orElseThrow(() -> new GrupoEnlaceNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewGrupo(returnValue.getGrupoId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link GrupoEnlace}.
   *
   * @param id Id del {@link GrupoEnlace}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, GrupoEnlace.class);

    Optional<GrupoEnlace> grupoEnlace = repository.findById(id);

    if (grupoEnlace.isPresent()) {
      authorityHelper.checkUserHasAuthorityViewGrupo(grupoEnlace.get().getGrupoId());
    } else {
      throw new GrupoEnlaceNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades {@link GrupoEnlace} paginadas y/o
   * filtradas del
   * {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoEnlace} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoEnlace> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoEnlace> specs = GrupoEnlaceSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoEnlace> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

}
