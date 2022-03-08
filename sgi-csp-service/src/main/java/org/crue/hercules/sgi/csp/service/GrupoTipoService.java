package org.crue.hercules.sgi.csp.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GrupoTipoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.crue.hercules.sgi.csp.repository.GrupoTipoRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoTipoSpecifications;
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
 * Service para la gestión de {@link GrupoTipo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoTipoService {

  private final GrupoTipoRepository repository;

  /**
   * Guarda la entidad {@link GrupoTipo}.
   * 
   * @param grupoTipo la entidad {@link GrupoTipo} a guardar.
   * @return la entidad {@link GrupoTipo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoTipo create(GrupoTipo grupoTipo) {
    log.debug("create(GrupoTipo grupoTipo) - start");

    AssertHelper.idIsNull(grupoTipo.getId(), GrupoTipo.class);
    GrupoTipo returnValue = repository.save(grupoTipo);

    log.debug("create(GrupoTipo grupoTipo) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link GrupoTipo}.
   *
   * @param grupoTipoActualizar {@link GrupoTipo} con los datos actualizados.
   * @return {@link GrupoTipo} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public GrupoTipo update(@Valid GrupoTipo grupoTipoActualizar) {
    log.debug("update(GrupoTipo grupoTipoActualizar) - start");

    AssertHelper.idNotNull(grupoTipoActualizar.getId(), GrupoTipo.class);

    return repository.findById(grupoTipoActualizar.getId()).map(data -> {
      data.setFechaInicio(grupoTipoActualizar.getFechaInicio());
      data.setFechaFin(grupoTipoActualizar.getFechaFin());
      data.setTipo(grupoTipoActualizar.getTipo());

      GrupoTipo returnValue = repository.save(data);

      log.debug("update(GrupoTipo grupoTipoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoTipoNotFoundException(grupoTipoActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link GrupoTipo} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoTipo}.
   * @return la entidad {@link GrupoTipo}.
   */
  public GrupoTipo findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoTipo.class);
    final GrupoTipo returnValue = repository.findById(id).orElseThrow(() -> new GrupoTipoNotFoundException(id));

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link GrupoTipo}.
   *
   * @param id Id del {@link GrupoTipo}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, GrupoTipo.class);

    if (!repository.existsById(id)) {
      throw new GrupoTipoNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades {@link GrupoTipo} paginadas y/o filtradas del
   * {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoTipo} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoTipo> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    Specification<GrupoTipo> specs = GrupoTipoSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoTipo> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

}
