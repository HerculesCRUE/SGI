package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.exceptions.GastoRequerimientoJustificacionNotFoundException;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.crue.hercules.sgi.csp.repository.GastoRequerimientoJustificacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.GastoRequerimientoJustificacionSpecifications;
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
 * {@link GastoRequerimientoJustificacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GastoRequerimientoJustificacionService {

  private static final String MESSAGE_KEY_REQUERIMIENTO_JUSTIFICACION_ID = "requerimientoJustificacionId";

  private final GastoRequerimientoJustificacionRepository repository;

  /**
   * Obtener todas las entidades {@link GastoRequerimientoJustificacion}
   * pertenecientes al {@link RequerimientoJustificacion} paginadas y/o filtradas.
   *
   * @param requerimientoJustificacionId el identificador de un
   *                                     {@link RequerimientoJustificacion}
   * @param pageable                     la información de la paginación.
   * @param query                        la información del filtro.
   * @return la lista de entidades {@link GastoRequerimientoJustificacion}
   *         paginadas y/o filtradas.
   */
  public Page<GastoRequerimientoJustificacion> findAllByRequerimientoJustificacionId(
      Long requerimientoJustificacionId, String query, Pageable pageable) {
    log.debug(
        "findAllByRequerimientoJustificacionId(Long requerimientoJustificacionId, String query, Pageable pageable) - start");
    Specification<GastoRequerimientoJustificacion> specs = GastoRequerimientoJustificacionSpecifications
        .byRequerimientoJustificacionId(requerimientoJustificacionId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<GastoRequerimientoJustificacion> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByRequerimientoJustificacionId(Long requerimientoJustificacionId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link GastoRequerimientoJustificacion}.
   *
   * @param gastoRequerimientoJustificacion la entidad
   *                                        {@link GastoRequerimientoJustificacion}
   *                                        a
   *                                        guardar.
   * @return la entidad {@link GastoRequerimientoJustificacion} persistida.
   */
  @Transactional
  public GastoRequerimientoJustificacion create(GastoRequerimientoJustificacion gastoRequerimientoJustificacion) {
    log.debug("create(GastoRequerimientoJustificacion gastoRequerimientoJustificacion) - start");
    AssertHelper.idIsNull(gastoRequerimientoJustificacion.getId(), GastoRequerimientoJustificacion.class);

    AssertHelper.fieldNotNull(gastoRequerimientoJustificacion.getRequerimientoJustificacionId(),
        GastoRequerimientoJustificacion.class, MESSAGE_KEY_REQUERIMIENTO_JUSTIFICACION_ID);

    GastoRequerimientoJustificacion returnValue = repository.save(gastoRequerimientoJustificacion);

    log.debug("create(GastoRequerimientoJustificacion gastoRequerimientoJustificacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link GastoRequerimientoJustificacion}.
   *
   * @param gastoRequerimientoJustificacion la entidad
   *                                        {@link GastoRequerimientoJustificacion}
   *                                        a actualizar.
   * @return la entidad {@link GastoRequerimientoJustificacion} persistida.
   */
  @Transactional
  public GastoRequerimientoJustificacion update(GastoRequerimientoJustificacion gastoRequerimientoJustificacion) {
    log.debug("update(GastoRequerimientoJustificacion gastoRequerimientoJustificacion) - start");

    AssertHelper.idNotNull(gastoRequerimientoJustificacion.getId(), GastoRequerimientoJustificacion.class);

    return repository.findById(gastoRequerimientoJustificacion.getId())
        .map(gastoRequerimientoJustificacionExistente -> {

          // Establecemos los campos actualizables con los recibidos
          gastoRequerimientoJustificacionExistente
              .setAceptado(gastoRequerimientoJustificacion.getAceptado());
          gastoRequerimientoJustificacionExistente
              .setAlegacion(gastoRequerimientoJustificacion.getAlegacion());
          gastoRequerimientoJustificacionExistente
              .setImporteAceptado(gastoRequerimientoJustificacion.getImporteAceptado());
          gastoRequerimientoJustificacionExistente
              .setImporteAlegado(gastoRequerimientoJustificacion.getImporteAlegado());
          gastoRequerimientoJustificacionExistente
              .setImporteRechazado(gastoRequerimientoJustificacion.getImporteRechazado());
          gastoRequerimientoJustificacionExistente
              .setIncidencia(gastoRequerimientoJustificacion.getIncidencia());

          // Actualizamos la entidad
          GastoRequerimientoJustificacion returnValue = repository.save(gastoRequerimientoJustificacionExistente);

          log.debug("update(GastoRequerimientoJustificacion gastoRequerimientoJustificacion) - end");
          return returnValue;
        }).orElseThrow(
            () -> new GastoRequerimientoJustificacionNotFoundException(gastoRequerimientoJustificacion.getId()));
  }

  /**
   * Elimina el {@link GastoRequerimientoJustificacion}.
   *
   * @param id Id del {@link GastoRequerimientoJustificacion}.
   */
  @Transactional
  public void deleteById(Long id) {
    log.debug("deleteById(Long id) - start");

    AssertHelper.idNotNull(id, GastoRequerimientoJustificacion.class);

    repository.deleteById(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Elimina las entidades {@link GastoRequerimientoJustificacion} con el
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
   * Busca todos los objetos de tipo {@link GastoRequerimientoJustificacion}
   * 
   * @param query  filtro
   * @param paging ordenación y página
   * @return lista de objetos de tipo {@link GastoRequerimientoJustificacion}
   */
  public Page<GastoRequerimientoJustificacion> findAll(String query,
      Pageable paging) {
    Specification<GastoRequerimientoJustificacion> specs = SgiRSQLJPASupport.toSpecification(query);

    return repository.findAll(specs, paging);
  }

  /**
   * Comprueba si existe algun {@link RequerimientoJustificacion} vinculado a la
   * entidad {@link ProyectoPeriodoJustificacion}.
   *
   * @param identificadorJustificacion Identificador de justificacion.
   * @return true si existe una o mas / false en caso contrario.
   */
  public boolean existsWithIndentificadorJustificacion(String identificadorJustificacion) {
    log.debug("existsWithIndentificadorJustificacion(String identificadorJustificacion) - start");
    boolean exists = !repository.existsByIdentificadorJustificacion(identificadorJustificacion);
    log.debug("existsWithIndentificadorJustificacion(String identificadorJustificacion) - end");
    return exists;
  }

}
