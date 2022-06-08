package org.crue.hercules.sgi.csp.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.exceptions.GrupoEquipoInstrumentalNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.GrupoLineaEquipoInstrumentalNotFoundException;
import org.crue.hercules.sgi.csp.model.GrupoLineaEquipoInstrumental;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.repository.GrupoEquipoInstrumentalRepository;
import org.crue.hercules.sgi.csp.repository.GrupoLineaEquipoInstrumentalRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoLineaEquipoInstrumentalSpecifications;
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
 * Service para la gestión de {@link GrupoLineaEquipoInstrumental}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class GrupoLineaEquipoInstrumentalService {

  private final GrupoLineaEquipoInstrumentalRepository repository;
  private final GrupoEquipoInstrumentalRepository grupoEquipoInstrumentalRepository;
  private final GrupoLineaInvestigacionAuthorityHelper authorityHelper;

  /**
   * Obtiene una entidad {@link GrupoLineaEquipoInstrumental} por id.
   * 
   * @param id Identificador de la entidad {@link GrupoLineaEquipoInstrumental}.
   * @return la entidad {@link GrupoLineaEquipoInstrumental}.
   */
  public GrupoLineaEquipoInstrumental findById(Long id) {
    log.debug("findById(Long id) - start");

    AssertHelper.idNotNull(id, GrupoLineaEquipoInstrumental.class);
    final GrupoLineaEquipoInstrumental returnValue = repository.findById(id)
        .orElseThrow(() -> new GrupoLineaEquipoInstrumentalNotFoundException(id));

    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(returnValue.getGrupoLineaInvestigacionId());

    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link GrupoLineaEquipoInstrumental} paginadas
   * y/o
   * filtradas del {@link GrupoLineaInvestigacion}.
   *
   * @param grupoLineaInvestigacionId Identificador de la entidad
   *                                  {@link GrupoLineaInvestigacion}.
   * @param paging                    la información de la paginación.
   * @param query                     la información del filtro.
   * @return la lista de entidades {@link GrupoLineaEquipoInstrumental} paginadas
   *         y/o
   *         filtradas.
   */
  public Page<GrupoLineaEquipoInstrumental> findAllByGrupoLineaInvestigacion(Long grupoLineaInvestigacionId,
      String query,
      Pageable paging) {
    log.debug("findAll(Long grupoId, String query, Pageable paging) - start");
    AssertHelper.idNotNull(grupoLineaInvestigacionId, GrupoLineaInvestigacion.class);
    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(grupoLineaInvestigacionId);

    Specification<GrupoLineaEquipoInstrumental> specs = GrupoLineaEquipoInstrumentalSpecifications
        .byGrupoLineaInvestigacionId(
            grupoLineaInvestigacionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoLineaEquipoInstrumental> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(Long grupoId, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Actualiza el listado de {@link GrupoLineaEquipoInstrumental} de la
   * {@link GrupoLineaInvestigacion}
   * con el
   * listado grupoLineasEquiposInstrumentales añadiendo, editando o eliminando los
   * elementos segun
   * proceda.
   *
   * @param grupoLineaInvestigacionId        Id de la
   *                                         {@link GrupoLineaInvestigacion}.
   * @param grupoLineasEquiposInstrumentales lista con los nuevos
   *                                         {@link GrupoLineaEquipoInstrumental}
   *                                         a
   *                                         guardar.
   * @return la entidad {@link GrupoLineaEquipoInstrumental} persistida.
   */
  @Transactional
  public List<GrupoLineaEquipoInstrumental> update(Long grupoLineaInvestigacionId,
      @Valid List<GrupoLineaEquipoInstrumental> grupoLineasEquiposInstrumentales) {
    log.debug(
        "update(Long grupoLineaInvestigacionId, List<GrupoLineaEquipoInstrumental> grupoLineasEquiposInstrumentales) - start");

    AssertHelper.idNotNull(grupoLineaInvestigacionId, GrupoLineaInvestigacion.class);
    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(grupoLineaInvestigacionId);

    List<GrupoLineaEquipoInstrumental> grupoLineasEquiposInstrumentalesBD = repository
        .findAllByGrupoLineaInvestigacionId(
            grupoLineaInvestigacionId);

    // Miembros del equipoInstrumental eliminados
    List<GrupoLineaEquipoInstrumental> grupoLineasEquiposInstrumentalesEliminar = grupoLineasEquiposInstrumentalesBD
        .stream()
        .filter(grupoLineaEquipoInstrumental -> grupoLineasEquiposInstrumentales.stream()
            .map(GrupoLineaEquipoInstrumental::getId)
            .noneMatch(id -> Objects.equals(id, grupoLineaEquipoInstrumental.getId())))
        .collect(Collectors.toList());

    if (!grupoLineasEquiposInstrumentalesEliminar.isEmpty()) {
      repository.deleteAll(grupoLineasEquiposInstrumentalesEliminar);
    }

    List<GrupoLineaEquipoInstrumental> returnValue = repository.saveAll(grupoLineasEquiposInstrumentales);
    log.debug(
        "update(Long grupoLineaInvestigacionId, List<GrupoLineaEquipoInstrumental> grupoLineasEquiposInstrumentales) - end");

    return returnValue;
  }

  public boolean existsGrupoLineaEquipoInstrumentalInGrupoEquipoInstrumental(Long idGrupoEquipoInstrumental) {
    if (!grupoEquipoInstrumentalRepository.existsById(idGrupoEquipoInstrumental)) {
      throw new GrupoEquipoInstrumentalNotFoundException(idGrupoEquipoInstrumental);
    }

    Specification<GrupoLineaEquipoInstrumental> specGrupoEquipoInstrumental = GrupoLineaEquipoInstrumentalSpecifications
        .byGrupoEquipoInstrumentalId(idGrupoEquipoInstrumental);
    Specification<GrupoLineaEquipoInstrumental> specs = Specification.where(
        specGrupoEquipoInstrumental);

    return repository.count(specs) > 0;
  }

}
