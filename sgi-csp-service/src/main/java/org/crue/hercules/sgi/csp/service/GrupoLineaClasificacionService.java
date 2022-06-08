package org.crue.hercules.sgi.csp.service;

import java.util.Optional;

import org.crue.hercules.sgi.csp.exceptions.GrupoLineaClasificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.crue.hercules.sgi.csp.repository.GrupoLineaClasificacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoLineaClasificacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.GrupoLineaInvestigacionAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link GrupoLineaClasificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GrupoLineaClasificacionService {

  private final GrupoLineaClasificacionRepository repository;
  private final GrupoLineaInvestigacionAuthorityHelper authorityHelper;

  /**
   * Guardar un nuevo {@link GrupoLineaClasificacion}.
   *
   * @param grupoLineaClasificacion la entidad {@link GrupoLineaClasificacion} a
   *                                guardar.
   * @return la entidad {@link GrupoLineaClasificacion} persistida.
   */
  @Transactional
  public GrupoLineaClasificacion create(GrupoLineaClasificacion grupoLineaClasificacion) {
    log.debug("create(GrupoLineaClasificacion grupoLineaClasificacion) - start");

    AssertHelper.idIsNull(grupoLineaClasificacion.getId(), GrupoLineaClasificacion.class);
    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(grupoLineaClasificacion.getId());

    GrupoLineaClasificacion returnValue = repository.save(grupoLineaClasificacion);

    log.debug("create(GrupoLineaClasificacion grupoLineaClasificacion) - end");
    return returnValue;
  }

  /**
   * Elimina el {@link GrupoLineaClasificacion}.
   *
   * @param id Id del {@link GrupoLineaClasificacion}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, GrupoLineaClasificacion.class);

    Optional<GrupoLineaClasificacion> grupoLineaClasificacion = repository.findById(id);

    if (grupoLineaClasificacion.isPresent()) {
      authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(
          grupoLineaClasificacion.get().getGrupoLineaInvestigacionId());
    } else {
      throw new GrupoLineaClasificacionNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene los {@link GrupoLineaClasificacion} para un
   * {@link GrupoLineaInvestigacion}.
   *
   * @param grupoLineaInvestigacionId el id de la {@link GrupoLineaInvestigacion}.
   * @param query                     la información del filtro.
   * @param pageable                  la información de la paginación.
   * @return la lista de entidades {@link GrupoLineaClasificacion} de la
   *         {@link GrupoLineaInvestigacion} paginadas.
   */
  public Page<GrupoLineaClasificacion> findAllByGrupoLineaInvestigacion(Long grupoLineaInvestigacionId, String query,
      Pageable pageable) {
    log.debug(
        "findAllByGrupoLineaInvestigacion(Long grupoLineaInvestigacionId, String query, Pageable pageable) - start");
    authorityHelper.checkUserHasAuthorityViewGrupoLineaInvestigacion(grupoLineaInvestigacionId);

    Specification<GrupoLineaClasificacion> specs = GrupoLineaClasificacionSpecifications.byGrupoLineaInvestigacionId(
        grupoLineaInvestigacionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoLineaClasificacion> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByGrupoLineaInvestigacion(Long grupoLineaInvestigacionId, String query, Pageable pageable) - end");
    return returnValue;
  }

}