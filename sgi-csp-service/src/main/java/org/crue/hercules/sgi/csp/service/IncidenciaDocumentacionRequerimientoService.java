package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.IncidenciaDocumentacionRequerimientoNotFoundException;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.IncidenciaDocumentacionRequerimientoRepository;
import org.crue.hercules.sgi.csp.repository.specification.IncidenciaDocumentacionRequerimientoSpecifications;
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
 * Service Implementation para gestion
 * {@link IncidenciaDocumentacionRequerimiento}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IncidenciaDocumentacionRequerimientoService {

  private final IncidenciaDocumentacionRequerimientoRepository repository;

  /**
   * Obtener todas las entidades {@link IncidenciaDocumentacionRequerimiento}
   * pertenecientes al {@link RequerimientoJustificacion} paginadas y/o filtradas.
   *
   * @param requerimientoJustificacionId el identificador de un
   *                                     {@link RequerimientoJustificacion}
   * @param pageable                     la información de la paginación.
   * @param query                        la información del filtro.
   * @return la lista de entidades {@link IncidenciaDocumentacionRequerimiento}
   *         paginadas y/o filtradas.
   */
  public Page<IncidenciaDocumentacionRequerimiento> findAllByRequerimientoJustificacionId(
      Long requerimientoJustificacionId, String query, Pageable pageable) {
    log.debug(
        "findAllByRequerimientoJustificacionId(Long requerimientoJustificacionId, String query, Pageable pageable) - start");
    Specification<IncidenciaDocumentacionRequerimiento> specs = IncidenciaDocumentacionRequerimientoSpecifications
        .byRequerimientoJustificacionId(requerimientoJustificacionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<IncidenciaDocumentacionRequerimiento> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByRequerimientoJustificacionId(Long requerimientoJustificacionId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Elimina la entidad {@link IncidenciaDocumentacionRequerimiento}.
   *
   * @param id identificador de la entidad
   *           {@link IncidenciaDocumentacionRequerimiento}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");
    AssertHelper.idNotNull(id, IncidenciaDocumentacionRequerimiento.class);

    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Elimina las entidades {@link IncidenciaDocumentacionRequerimiento} con el
   * requerimientoJustificacionId indicado.
   *
   * @param requerimientoJustificacionId identificador de la entidad
   *                                     {@link RequerimientoJustificacion}.
   */
  @Transactional
  public void deleteByRequerimientoJustificacionId(Long requerimientoJustificacionId) {
    log.debug("deleteByRequerimientoJustificacionId(Long requerimientoJustificacionId) - start");
    AssertHelper.idNotNull(requerimientoJustificacionId, RequerimientoJustificacion.class);

    repository.deleteInBulkByRequerimientoJustificacionId(requerimientoJustificacionId);
    log.debug("deleteByRequerimientoJustificacionId(Long requerimientoJustificacionId) - end");
  }

  /**
   * Guardar un nuevo {@link IncidenciaDocumentacionRequerimiento}.
   *
   * @param incidenciaDocumentacionRequerimiento la entidad
   *                                             {@link IncidenciaDocumentacionRequerimiento}
   *                                             a
   *                                             guardar.
   * @return la entidad {@link IncidenciaDocumentacionRequerimiento} persistida.
   */
  @Transactional
  public IncidenciaDocumentacionRequerimiento create(
      IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) {
    log.debug("create(IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) - start");
    AssertHelper.idIsNull(incidenciaDocumentacionRequerimiento.getId(), IncidenciaDocumentacionRequerimiento.class);

    IncidenciaDocumentacionRequerimiento returnValue = repository.save(incidenciaDocumentacionRequerimiento);

    log.debug("create(IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) - end");
    return returnValue;
  }

  /**
   * Actualizar los campos de una entidad
   * {@link IncidenciaDocumentacionRequerimiento}.
   *
   * @param incidenciaDocumentacionRequerimiento la entidad
   *                                             {@link IncidenciaDocumentacionRequerimiento}
   *                                             a actualizar.
   * @return la entidad {@link IncidenciaDocumentacionRequerimiento} persistida.
   */
  @Transactional
  public IncidenciaDocumentacionRequerimiento update(
      IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) {
    log.debug("update(IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) - start");

    AssertHelper.idNotNull(incidenciaDocumentacionRequerimiento.getId(), IncidenciaDocumentacionRequerimiento.class);

    return repository.findById(incidenciaDocumentacionRequerimiento.getId())
        .map(incidenciaDocumentacionRequerimientoExistente -> {

          // Establecemos los campos actualizables con los recibidos
          incidenciaDocumentacionRequerimientoExistente
              .setIncidencia(incidenciaDocumentacionRequerimiento.getIncidencia());
          incidenciaDocumentacionRequerimientoExistente
              .setNombreDocumento(incidenciaDocumentacionRequerimiento.getNombreDocumento());

          // Actualizamos la entidad
          IncidenciaDocumentacionRequerimiento returnValue = repository
              .save(incidenciaDocumentacionRequerimientoExistente);

          log.debug(
              "update(IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) - end");
          return returnValue;
        }).orElseThrow(() -> new IncidenciaDocumentacionRequerimientoNotFoundException(
            incidenciaDocumentacionRequerimiento.getId()));
  }

  /**
   * Actualizar el campo alegacion de la entidad
   * {@link IncidenciaDocumentacionRequerimiento}.
   *
   * @param incidenciaDocumentacionRequerimiento la entidad
   *                                             {@link IncidenciaDocumentacionRequerimiento}
   *                                             a actualizar.
   * @return la entidad {@link IncidenciaDocumentacionRequerimiento} persistida.
   */
  @Transactional
  public IncidenciaDocumentacionRequerimiento updateAlegacion(
      IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) {
    log.debug("updateAlegacion(IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) - start");

    AssertHelper.idNotNull(incidenciaDocumentacionRequerimiento.getId(), IncidenciaDocumentacionRequerimiento.class);

    return repository.findById(incidenciaDocumentacionRequerimiento.getId())
        .map(incidenciaDocumentacionRequerimientoExistente -> {

          // Establecemos los campos actualizables con los recibidos
          incidenciaDocumentacionRequerimientoExistente
              .setAlegacion(incidenciaDocumentacionRequerimiento.getAlegacion());

          // Actualizamos la entidad
          IncidenciaDocumentacionRequerimiento returnValue = repository
              .save(incidenciaDocumentacionRequerimientoExistente);

          log.debug(
              "updateAlegacion(IncidenciaDocumentacionRequerimiento incidenciaDocumentacionRequerimiento) - end");
          return returnValue;
        }).orElseThrow(() -> new IncidenciaDocumentacionRequerimientoNotFoundException(
            incidenciaDocumentacionRequerimiento.getId()));
  }
}
