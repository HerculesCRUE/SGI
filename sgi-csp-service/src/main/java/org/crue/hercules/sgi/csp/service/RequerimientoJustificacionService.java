package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.RequerimientoJustificacionNotDeleteableException;
import org.crue.hercules.sgi.csp.exceptions.RequerimientoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.RequerimientoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.RequerimientoJustificacionSpecifications;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link RequerimientoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequerimientoJustificacionService {

  private final RequerimientoJustificacionRepository repository;

  /**
   * Obtener todas las entidades {@link RequerimientoJustificacion} pertenecientes
   * al ProyectoSgeRef paginadas y/o filtradas.
   *
   * @param proyectoSgeRef el identificador de un ProyectoSGE
   * @param pageable       la información de la paginación.
   * @param query          la información del filtro.
   * @return la lista de entidades {@link RequerimientoJustificacion} paginadas
   *         y/o filtradas.
   */
  public Page<RequerimientoJustificacion> findAllByProyectoSgeRef(String proyectoSgeRef, String query,
      Pageable pageable) {
    log.debug("findAllByProyectoSgeRef(String query, Pageable pageable) - start");
    Specification<RequerimientoJustificacion> specs = RequerimientoJustificacionSpecifications
        .byProyectoProyectoSgeProyectoSgeRef(proyectoSgeRef).and(SgiRSQLJPASupport.toSpecification(query));

    Page<RequerimientoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProyectoSgeRef(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link RequerimientoJustificacion} por su id.
   *
   * @param id el id de la entidad {@link RequerimientoJustificacion}.
   * @return la entidad {@link RequerimientoJustificacion}.
   */
  public RequerimientoJustificacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    AssertHelper.idNotNull(id, RequerimientoJustificacion.class);

    final RequerimientoJustificacion returnValue = repository.findById(id)
        .orElseThrow(() -> new RequerimientoJustificacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la entidad {@link RequerimientoJustificacion}.
   *
   * @param id identificador de la entidad {@link RequerimientoJustificacion}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    AssertHelper.idNotNull(id, RequerimientoJustificacion.class);

    if (repository.count(RequerimientoJustificacionSpecifications.byRequerimientoPrevioId(id)) > 0) {
      throw new RequerimientoJustificacionNotDeleteableException();
    }

    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }
}
