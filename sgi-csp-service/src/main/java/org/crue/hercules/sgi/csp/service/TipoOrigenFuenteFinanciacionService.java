package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoOrigenFuenteFinanciacionNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.repository.TipoOrigenFuenteFinanciacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoOrigenFuenteFinanciacionSpecification;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link TipoOrigenFuenteFinanciacion}.
 */

@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class TipoOrigenFuenteFinanciacionService {

  private final Validator validator;

  private final TipoOrigenFuenteFinanciacionRepository repository;

  public TipoOrigenFuenteFinanciacionService(Validator validator,
      TipoOrigenFuenteFinanciacionRepository fuenteFinanciacionRepository) {
    this.validator = validator;
    this.repository = fuenteFinanciacionRepository;
  }

  /**
   * Guardar un nuevo {@link TipoOrigenFuenteFinanciacion}.
   *
   * @param tipoOrigenfuenteFinanciacion la entidad
   *                                     {@link TipoOrigenFuenteFinanciacion} a
   *                                     guardar.
   * @return la entidad {@link TipoOrigenFuenteFinanciacion} persistida.
   */
  @Transactional
  @Validated({ TipoOrigenFuenteFinanciacion.OnCrear.class })
  public TipoOrigenFuenteFinanciacion create(@Valid TipoOrigenFuenteFinanciacion tipoOrigenfuenteFinanciacion) {
    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - start");

    AssertHelper.idIsNull(tipoOrigenfuenteFinanciacion.getId(), TipoOrigenFuenteFinanciacion.class);

    tipoOrigenfuenteFinanciacion.setActivo(true);
    TipoOrigenFuenteFinanciacion returnValue = repository.save(tipoOrigenfuenteFinanciacion);

    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoOrigenFuenteFinanciacion}.
   *
   * @param tipoOrigenfuenteFinanciacion la entidad
   *                                     {@link TipoOrigenFuenteFinanciacion} a
   *                                     actualizar.
   * @return la entidad {@link TipoOrigenFuenteFinanciacion} persistida.
   */
  @Transactional
  @Validated({ TipoOrigenFuenteFinanciacion.OnActualizar.class })
  public TipoOrigenFuenteFinanciacion update(@Valid TipoOrigenFuenteFinanciacion tipoOrigenfuenteFinanciacion) {
    log.debug("update(TipoOrigenFuenteFinanciacion tipoOrigenFuenteFinanciacion) - start");

    AssertHelper.idNotNull(tipoOrigenfuenteFinanciacion.getId(), TipoOrigenFuenteFinanciacion.class);

    return repository.findById(tipoOrigenfuenteFinanciacion.getId()).map(fuenteFinanciacionExistente -> {

      // Establecemos los campos actualizables con los recibidos
      fuenteFinanciacionExistente.setNombre(tipoOrigenfuenteFinanciacion.getNombre());

      // Actualizamos la entidad
      TipoOrigenFuenteFinanciacion returnValue = repository.save(fuenteFinanciacionExistente);
      log.debug("update(TipoOrigenFuenteFinanciacion tipoOrigenfuenteFinanciacion) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoOrigenFuenteFinanciacionNotFoundException(tipoOrigenfuenteFinanciacion.getId()));
  }

  /**
   * Activa el {@link TipoOrigenFuenteFinanciacion}.
   *
   * @param id Id del {@link TipoOrigenFuenteFinanciacion}.
   * @return la entidad {@link TipoOrigenFuenteFinanciacion} persistida.
   */
  @Transactional
  public TipoOrigenFuenteFinanciacion activar(Long id) {
    log.debug("activar(Long id) - start");

    AssertHelper.idNotNull(id, TipoOrigenFuenteFinanciacion.class);

    return repository.findById(id).map(tipoOrigenFuenteFinanciacion -> {
      if (tipoOrigenFuenteFinanciacion.getActivo().booleanValue()) {
        // Si esta activo no se hace nada
        return tipoOrigenFuenteFinanciacion;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoOrigenFuenteFinanciacion>> result = validator.validate(tipoOrigenFuenteFinanciacion,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoOrigenFuenteFinanciacion.setActivo(true);

      TipoOrigenFuenteFinanciacion returnValue = repository.save(tipoOrigenFuenteFinanciacion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoOrigenFuenteFinanciacionNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoOrigenFuenteFinanciacion}.
   *
   * @param id Id del {@link TipoOrigenFuenteFinanciacion}.
   * @return la entidad {@link TipoOrigenFuenteFinanciacion} persistida.
   */
  @Transactional
  public TipoOrigenFuenteFinanciacion desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    AssertHelper.idNotNull(id, TipoOrigenFuenteFinanciacion.class);

    return repository.findById(id).map(tipoOrigenFuenteFinanciacion -> {
      if (!tipoOrigenFuenteFinanciacion.getActivo().booleanValue()) {
        // Si no esta activo no se hace nada
        return tipoOrigenFuenteFinanciacion;
      }

      tipoOrigenFuenteFinanciacion.setActivo(false);

      TipoOrigenFuenteFinanciacion returnValue = repository.save(tipoOrigenFuenteFinanciacion);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoOrigenFuenteFinanciacionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link TipoOrigenFuenteFinanciacion} activos
   * paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoOrigenFuenteFinanciacion} paginadas
   *         y/o
   *         filtradas.
   */
  public Page<TipoOrigenFuenteFinanciacion> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<TipoOrigenFuenteFinanciacion> specs = TipoOrigenFuenteFinanciacionSpecification.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoOrigenFuenteFinanciacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoOrigenFuenteFinanciacion} paginadas
   * y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoOrigenFuenteFinanciacion} paginadas
   *         y/o
   *         filtradas.
   */
  public Page<TipoOrigenFuenteFinanciacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoOrigenFuenteFinanciacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoOrigenFuenteFinanciacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoOrigenFuenteFinanciacion} por su id.
   *
   * @param id el id de la entidad {@link TipoOrigenFuenteFinanciacion}.
   * @return la entidad {@link TipoOrigenFuenteFinanciacion}.
   */
  public TipoOrigenFuenteFinanciacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoOrigenFuenteFinanciacion returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoOrigenFuenteFinanciacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
