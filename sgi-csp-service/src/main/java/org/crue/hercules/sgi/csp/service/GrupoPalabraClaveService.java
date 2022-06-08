package org.crue.hercules.sgi.csp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.Grupo;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave;
import org.crue.hercules.sgi.csp.repository.GrupoPalabraClaveRepository;
import org.crue.hercules.sgi.csp.repository.specification.GrupoPalabraClaveSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.GrupoAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link GrupoPalabraClave}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GrupoPalabraClaveService {

  private final GrupoPalabraClaveRepository repository;
  private final GrupoAuthorityHelper authorityHelper;

  /**
   * Obtiene los {@link GrupoPalabraClave} para una entidad
   * {@link Grupo} paginadas y/o filtradas.
   *
   * @param grupoId  el id de la {@link Grupo}.
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de {@link GrupoPalabraClave} de la
   *         {@link Grupo} paginadas y/o filtradas.
   */
  public Page<GrupoPalabraClave> findByGrupoId(Long grupoId, String query, Pageable pageable) {
    log.debug("findByGrupoId(Long grupoId, String query, Pageable pageable) - start");

    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    Specification<GrupoPalabraClave> specs = GrupoPalabraClaveSpecifications.byGrupoId(grupoId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GrupoPalabraClave> returnValue = repository.findAll(specs, pageable);
    log.debug("findByGrupoId(Long grupoId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Actualiza la lista de {@link GrupoPalabraClave} de la entidad
   * {@link Grupo}, elimina los pre-existentes y añade la nueva lista.
   * 
   * @param grupoId       el id del {@link Grupo}.
   * @param palabrasClave la lista con las nuevas palabras claves.
   * @return La lista actualizada de {@link GrupoPalabraClave}.
   */
  @Transactional
  public List<GrupoPalabraClave> updatePalabrasClave(Long grupoId, List<GrupoPalabraClave> palabrasClave) {
    log.debug("updatePalabrasClave(Long grupoId, List<GrupoPalabraClave> palabrasClave) - start");

    AssertHelper.idNotNull(grupoId, Grupo.class);
    authorityHelper.checkUserHasAuthorityViewGrupo(grupoId);

    // Eliminamos las GrupoPalabraClave existentes para el grupoId dado
    repository.deleteInBulkByGrupoId(grupoId);

    List<GrupoPalabraClave> returnValue = new ArrayList<>();
    if (!palabrasClave.isEmpty()) {
      // Eliminamos duplicados de la nueva lista
      List<GrupoPalabraClave> uniquePalabrasClave = palabrasClave.stream().map(palabraClave -> {
        palabraClave.setGrupoId(grupoId);
        return palabraClave;
      }).distinct().collect(Collectors.toList());
      // Guardamos la nueva lista
      returnValue = repository.saveAll(uniquePalabrasClave);
    }

    log.debug("updatePalabrasClave(Long grupoId, List<GrupoPalabraClave> palabrasClave) - end");
    return returnValue;
  }

}
