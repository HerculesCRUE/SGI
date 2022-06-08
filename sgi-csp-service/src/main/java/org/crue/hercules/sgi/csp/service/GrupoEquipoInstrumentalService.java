package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GrupoEquipoInstrumentalNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental;
import org.crue.hercules.sgi.csp.repository.GrupoEquipoInstrumentalRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoEquipoInstrumentalSpecifications;
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
 * Service para la gestión de {@link GrupoEquipoInstrumental}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoEquipoInstrumentalService {

  private final GrupoEquipoInstrumentalRepository repository;
  private final GrupoAuthorityHelper authorityHelper;

  /**
   * Guarda la entidad {@link GrupoEquipoInstrumental}.
   * 
   * @param grupoEquipo la entidad {@link GrupoEquipoInstrumental} a guardar.
   * @return la entidad {@link GrupoEquipoInstrumental} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public GrupoEquipoInstrumental create(@Valid GrupoEquipoInstrumental grupoEquipo) {
    log.debug("create(GrupoEquipoInstrumental grupoEquipo) - start");

    AssertHelper.idIsNull(grupoEquipo.getId(), GrupoEquipoInstrumental.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoEquipo.getGrupoId());

    GrupoEquipoInstrumental returnValue = repository.save(grupoEquipo);

    log.debug("create(GrupoEquipoInstrumental grupoEquipo) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link GrupoEquipoInstrumental}.
   *
   * @param grupoEquipoActualizar {@link GrupoEquipoInstrumental} con los datos
   *                              actualizados.
   * @return {@link GrupoEquipoInstrumental} actualizado.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public GrupoEquipoInstrumental update(@Valid GrupoEquipoInstrumental grupoEquipoActualizar) {
    log.debug("update(GrupoEquipoInstrumental grupoEquipoActualizar) - start");

    AssertHelper.idNotNull(grupoEquipoActualizar.getId(), GrupoEquipoInstrumental.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoEquipoActualizar.getGrupoId());

    return repository.findById(grupoEquipoActualizar.getId()).map(data -> {
      data.setNombre(grupoEquipoActualizar.getNombre());
      data.setNumRegistro(grupoEquipoActualizar.getNumRegistro());
      data.setDescripcion(grupoEquipoActualizar.getDescripcion());

      GrupoEquipoInstrumental returnValue = repository.save(data);

      log.debug("update(GrupoEquipoInstrumental grupoEquipoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new GrupoEquipoInstrumentalNotFoundException(grupoEquipoActualizar.getId()));
  }

  /**
   * Obtiene una entidad {@link GrupoEquipoInstrumental} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoEquipoInstrumental}.
   * @return la entidad {@link GrupoEquipoInstrumental}.
   */
  public GrupoEquipoInstrumental findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoEquipoInstrumental.class);
    final GrupoEquipoInstrumental returnValue = repository.findById(id)
        .orElseThrow(() -> new GrupoEquipoInstrumentalNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewGrupo(returnValue.getGrupoId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link GrupoEquipoInstrumental}.
   *
   * @param id Id del {@link GrupoEquipoInstrumental}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, GrupoEquipoInstrumental.class);

    Optional<GrupoEquipoInstrumental> grupoEquipoInstrumental = repository.findById(id);

    if (grupoEquipoInstrumental.isPresent()) {
      authorityHelper.checkUserHasAuthorityViewGrupo(grupoEquipoInstrumental.get().getGrupoId());
    } else {
      throw new GrupoEquipoInstrumentalNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtener todas las entidades {@link GrupoEquipoInstrumental} paginadas y/o
   * filtradas del
   * {@link Grupo}.
   *
   * @param grupoId Identificador de la entidad {@link Grupo}.
   * @param paging  la información de la paginación.
   * @param query   la información del filtro.
   * @return la lista de entidades {@link GrupoEquipoInstrumental} paginadas y/o
   *         filtradas.
   */
  public Page<GrupoEquipoInstrumental> findAllByGrupo(Long grupoId, String query, Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoEquipoInstrumental> specs = GrupoEquipoInstrumentalSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoEquipoInstrumental> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoEquipoInstrumental} de la {@link Grupo}
   * con el
   * listado grupoEquiposInstrumentales añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   *
   * @param grupoId                    Id de la {@link Grupo}.
   * @param grupoEquiposInstrumentales lista con los nuevos
   *                                   {@link GrupoEquipoInstrumental} a
   *                                   guardar.
   * @return la entidad {@link GrupoEquipoInstrumental} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public List<GrupoEquipoInstrumental> update(Long grupoId,
      @Valid List<GrupoEquipoInstrumental> grupoEquiposInstrumentales) {
    log.debug("update(Long grupoId, List<GrupoEquipoInstrumental> grupoEquiposInstrumentales) - start");

    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    List<GrupoEquipoInstrumental> grupoEquiposBD = repository.findAllByGrupoId(grupoId);

    // Miembros del equipo eliminados
    List<GrupoEquipoInstrumental> grupoEquiposEliminar = grupoEquiposBD.stream()
        .filter(grupoEquipo -> grupoEquiposInstrumentales.stream().map(GrupoEquipoInstrumental::getId)
            .noneMatch(id -> Objects.equals(id, grupoEquipo.getId())))
        .collect(Collectors.toList());

    if (!grupoEquiposEliminar.isEmpty()) {
      repository.deleteAll(grupoEquiposEliminar);
    }

    List<GrupoEquipoInstrumental> returnValue = repository.saveAll(grupoEquiposInstrumentales);
    log.debug("update(Long grupoId, List<GrupoEquipoInstrumental> grupoEquiposInstrumentales) - END");

    return returnValue;
  }

}
