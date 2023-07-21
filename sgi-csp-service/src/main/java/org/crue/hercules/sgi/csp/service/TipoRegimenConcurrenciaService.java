package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoRegimenConcurrenciaNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.repository.TipoRegimenConcurrenciaRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoRegimenConcurrenciaSpecifications;
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
 * Service para gestionar {@link TipoRegimenConcurrencia}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class TipoRegimenConcurrenciaService {

  private final Validator validator;

  private final TipoRegimenConcurrenciaRepository repository;

  public TipoRegimenConcurrenciaService(Validator validator,
      TipoRegimenConcurrenciaRepository tipoRegimenConcurrenciaRepository) {
    this.validator = validator;
    this.repository = tipoRegimenConcurrenciaRepository;
  }

  /**
   * Guardar un nuevo {@link TipoRegimenConcurrencia}.
   *
   * @param tipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia} a
   *                                guardar.
   * @return la entidad {@link TipoRegimenConcurrencia} persistida.
   */
  @Transactional
  @Validated({ TipoRegimenConcurrencia.OnCrear.class })
  public TipoRegimenConcurrencia create(@Valid TipoRegimenConcurrencia tipoRegimenConcurrencia) {
    log.debug("create(TipoRegimenConcurrencia tipoRegimenConcurrencia) - start");

    AssertHelper.idIsNull(tipoRegimenConcurrencia.getId(), TipoRegimenConcurrencia.class);

    tipoRegimenConcurrencia.setActivo(true);
    TipoRegimenConcurrencia returnValue = repository.save(tipoRegimenConcurrencia);

    log.debug("create(TipoRegimenConcurrencia tipoRegimenConcurrencia) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoRegimenConcurrencia}.
   *
   * @param tipoRegimenConcurrencia la entidad {@link TipoRegimenConcurrencia} a
   *                                actualizar.
   * @return la entidad {@link TipoRegimenConcurrencia} persistida.
   */
  @Transactional
  @Validated({ TipoRegimenConcurrencia.OnActualizar.class })
  public TipoRegimenConcurrencia update(@Valid TipoRegimenConcurrencia tipoRegimenConcurrencia) {
    log.debug("update(TipoRegimenConcurrencia tipoRegimenConcurrencia) - start");

    AssertHelper.idNotNull(tipoRegimenConcurrencia.getId(), TipoRegimenConcurrencia.class);

    return repository.findById(tipoRegimenConcurrencia.getId()).map(tipoRegimenConcurrenciaExistente -> {

      // Establecemos los campos actualizables con los recibidos
      tipoRegimenConcurrenciaExistente.setNombre(tipoRegimenConcurrencia.getNombre());

      // Actualizamos la entidad
      TipoRegimenConcurrencia returnValue = repository.save(tipoRegimenConcurrenciaExistente);
      log.debug("update(TipoRegimenConcurrencia tipoRegimenConcurrencia) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoRegimenConcurrenciaNotFoundException(tipoRegimenConcurrencia.getId()));
  }

  /**
   * Activa el {@link TipoRegimenConcurrencia}.
   *
   * @param id Id del {@link TipoRegimenConcurrencia}.
   * @return la entidad {@link TipoRegimenConcurrencia} persistida.
   */
  @Transactional
  public TipoRegimenConcurrencia activar(Long id) {
    log.debug("activar(Long id) - start");

    AssertHelper.idNotNull(id, TipoRegimenConcurrencia.class);

    return repository.findById(id).map(tipoRegimenConcurrencia -> {
      if (Boolean.TRUE.equals(tipoRegimenConcurrencia.getActivo())) {
        // Si esta activo no se hace nada
        return tipoRegimenConcurrencia;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoRegimenConcurrencia>> result = validator.validate(tipoRegimenConcurrencia,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoRegimenConcurrencia.setActivo(true);

      TipoRegimenConcurrencia returnValue = repository.save(tipoRegimenConcurrencia);
      log.debug("activar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoRegimenConcurrenciaNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoRegimenConcurrencia}.
   *
   * @param id Id del {@link TipoRegimenConcurrencia}.
   * @return la entidad {@link TipoRegimenConcurrencia} persistida.
   */
  @Transactional
  public TipoRegimenConcurrencia desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    AssertHelper.idNotNull(id, TipoRegimenConcurrencia.class);

    return repository.findById(id).map(tipoRegimenConcurrencia -> {
      if (!tipoRegimenConcurrencia.getActivo()) {
        // Si no esta activo no se hace nada
        return tipoRegimenConcurrencia;
      }

      tipoRegimenConcurrencia.setActivo(false);

      TipoRegimenConcurrencia returnValue = repository.save(tipoRegimenConcurrencia);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoRegimenConcurrenciaNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link TipoRegimenConcurrencia} activos paginadas
   * y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoRegimenConcurrencia} paginadas y/o
   *         filtradas.
   */
  public Page<TipoRegimenConcurrencia> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<TipoRegimenConcurrencia> specs = TipoRegimenConcurrenciaSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoRegimenConcurrencia> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoRegimenConcurrencia} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoRegimenConcurrencia} paginadas y/o
   *         filtradas.
   */
  public Page<TipoRegimenConcurrencia> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoRegimenConcurrencia> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoRegimenConcurrencia> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoRegimenConcurrencia} por su id.
   *
   * @param id el id de la entidad {@link TipoRegimenConcurrencia}.
   * @return la entidad {@link TipoRegimenConcurrencia}.
   */
  public TipoRegimenConcurrencia findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoRegimenConcurrencia returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoRegimenConcurrenciaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}