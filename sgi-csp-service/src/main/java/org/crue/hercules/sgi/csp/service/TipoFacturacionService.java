package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoFacturacionNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.crue.hercules.sgi.csp.repository.TipoFacturacionRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoFacturacionSpecifications;
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
 * Service para gestionar {@link TipoFacturacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class TipoFacturacionService {

  private final Validator validator;
  private final TipoFacturacionRepository repository;

  /**
   * Guardar un nuevo {@link TipoFacturacion}.
   *
   * @param tipoFacturacion la entidad {@link TipoFacturacion} a
   *                        guardar.
   * @return la entidad {@link TipoFacturacion} persistida.
   */
  @Transactional
  @Validated({ TipoFacturacion.OnCrear.class })
  public TipoFacturacion create(@Valid TipoFacturacion tipoFacturacion) {
    log.debug("create(TipoFacturacion tipoFacturacion) - start");

    AssertHelper.idIsNull(tipoFacturacion.getId(), TipoFacturacion.class);

    tipoFacturacion.setActivo(true);
    TipoFacturacion returnValue = repository.save(tipoFacturacion);

    log.debug("create(TipoFacturacion tipoFacturacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoFacturacion}.
   *
   * @param tipoFacturacion la entidad {@link TipoFacturacion} a
   *                        actualizar.
   * @return la entidad {@link TipoFacturacion} persistida.
   */
  @Transactional
  @Validated({ TipoFacturacion.OnActualizar.class })
  public TipoFacturacion update(@Valid TipoFacturacion tipoFacturacion) {
    log.debug("update(TipoFacturacion tipoFacturacion) - start");

    AssertHelper.idNotNull(tipoFacturacion.getId(), TipoFacturacion.class);

    return repository.findById(tipoFacturacion.getId()).map(tipoFacturacionExistente -> {
      // Establecemos los campos actualizables con los recibidos
      tipoFacturacionExistente.setNombre(tipoFacturacion.getNombre());
      tipoFacturacionExistente.setIncluirEnComunicado(tipoFacturacion.isIncluirEnComunicado());
      // Actualizamos la entidad
      TipoFacturacion returnValue = repository.save(tipoFacturacionExistente);
      log.debug("update(TipoFacturacion tipoFacturacion) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFacturacionNotFoundException(tipoFacturacion.getId()));
  }

  /**
   * Activa el {@link TipoFacturacion}.
   *
   * @param id Id del {@link TipoFacturacion}.
   * @return la entidad {@link TipoFacturacion} persistida.
   */
  @Transactional
  public TipoFacturacion activar(Long id) {
    log.debug("activar(Long id) - start");

    AssertHelper.idNotNull(id, TipoFacturacion.class);

    return repository.findById(id).map(tipoFacturacion -> {
      if (Boolean.TRUE.equals(tipoFacturacion.getActivo())) {
        // Si esta activo no se hace nada
        return tipoFacturacion;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoFacturacion>> result = validator.validate(tipoFacturacion,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }
      tipoFacturacion.setActivo(true);
      TipoFacturacion returnValue = repository.save(tipoFacturacion);
      log.debug("activar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFacturacionNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoFacturacion}.
   *
   * @param id Id del {@link TipoFacturacion}.
   * @return la entidad {@link TipoFacturacion} persistida.
   */
  @Transactional
  public TipoFacturacion desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    AssertHelper.idNotNull(id, TipoFacturacion.class);

    return repository.findById(id).map(tipoFacturacion -> {
      if (Boolean.FALSE.equals(tipoFacturacion.getActivo())) {
        // Si no esta activo no se hace nada
        return tipoFacturacion;
      }
      tipoFacturacion.setActivo(false);
      TipoFacturacion returnValue = repository.save(tipoFacturacion);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFacturacionNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link TipoFacturacion} activos paginadas
   * y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoFacturacion} paginadas y/o
   *         filtradas.
   */
  public Page<TipoFacturacion> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<TipoFacturacion> specs = TipoFacturacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoFacturacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoFacturacion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoFacturacion} paginadas y/o
   *         filtradas.
   */
  public Page<TipoFacturacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoFacturacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoFacturacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoFacturacion} por su id.
   *
   * @param id el id de la entidad {@link TipoFacturacion}.
   * @return la entidad {@link TipoFacturacion}.
   */
  public TipoFacturacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoFacturacion returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoFacturacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}
