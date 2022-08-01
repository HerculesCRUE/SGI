package org.crue.hercules.sgi.eer.service;

import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.crue.hercules.sgi.eer.repository.TipoDocumentoRepository;
import org.crue.hercules.sgi.eer.repository.specification.TipoDocumentoSpecifications;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para la gestión de {@link TipoDocumento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipoDocumentoService {

  private final TipoDocumentoRepository repository;

  /**
   * Obtiene todas las entidades {@link TipoDocumento} con padreId a null activas
   * paginadas y/o filtradas.
   * 
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link TipoDocumento} activas paginadas y/o
   *         filtradas.
   */
  public Page<TipoDocumento> findTiposActivos(String query, Pageable paging) {
    log.debug("findTiposActivos(String query, Pageable paging) - start");

    Specification<TipoDocumento> specs = TipoDocumentoSpecifications.activos()
        .and(TipoDocumentoSpecifications.byPadreIdisNull())
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<TipoDocumento> returnValue = repository.findAll(specs, paging);

    log.debug("findTiposActivos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link TipoDocumento} activas paginadas y/o
   * filtradas y cuyo padre es el {@link TipoDocumento} con id indicado.
   * 
   * @param padreId identificador del {@link TipoDocumento} padre.
   * @param query   la información del filtro.
   * @param paging  la información de la paginación.
   * @return la lista de entidades {@link TipoDocumento}.
   */
  public Page<TipoDocumento> findSubtiposActivos(Long padreId, String query, Pageable paging) {
    log.debug("findSubtipos(Long padreId, String query, Pageable paging) - start");

    Specification<TipoDocumento> specs = TipoDocumentoSpecifications.activos()
        .and(TipoDocumentoSpecifications.byPadreId(padreId))
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<TipoDocumento> returnValue = repository.findAll(specs, paging);

    log.debug("findSubtipos(Long padreId, String query, Pageable paging) - end");
    return returnValue;
  }
}
