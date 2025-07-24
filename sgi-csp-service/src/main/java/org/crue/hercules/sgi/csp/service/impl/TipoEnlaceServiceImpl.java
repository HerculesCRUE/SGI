package org.crue.hercules.sgi.csp.service.impl;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoEnlaceNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.repository.TipoEnlaceRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoEnlaceSpecifications;
import org.crue.hercules.sgi.csp.service.TipoEnlaceService;
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
 * Service Implementation para gestion {@link TipoEnlace}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Validated
public class TipoEnlaceServiceImpl implements TipoEnlaceService {

  private final TipoEnlaceRepository repository;

  private final Validator validator;

  /**
   * Guarda la entidad {@link TipoEnlace}.
   * 
   * @param tipoEnlace la entidad {@link TipoEnlace} a guardar.
   * @return TipoEnlace la entidad {@link TipoEnlace} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public TipoEnlace create(@Valid TipoEnlace tipoEnlace) {
    log.debug("create(TipoEnlace tipoEnlace) - start");

    AssertHelper.idIsNull(tipoEnlace.getId(), TipoEnlace.class);

    tipoEnlace.setActivo(Boolean.TRUE);
    TipoEnlace returnValue = repository.save(tipoEnlace);

    log.debug("create(TipoEnlace tipoEnlace) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link TipoEnlace}.
   * 
   * @param tipoEnlace tipoEnlaceActualizar {@link TipoEnlace} con los datos
   *                   actualizados.
   * @return {@link TipoEnlace} actualizado.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public TipoEnlace update(@Valid TipoEnlace tipoEnlace) {
    log.debug("update(TipoEnlace tipoEnlace) - start");

    AssertHelper.idNotNull(tipoEnlace.getId(), TipoEnlace.class);

    return repository.findById(tipoEnlace.getId()).map(data -> {
      data.setNombre(tipoEnlace.getNombre());
      data.setDescripcion(tipoEnlace.getDescripcion());

      TipoEnlace returnValue = repository.save(data);
      log.debug("update(TipoEnlace tipoEnlace) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoEnlaceNotFoundException(tipoEnlace.getId()));
  }

  /**
   * Reactiva el {@link TipoEnlace}.
   *
   * @param id Id del {@link TipoEnlace}.
   * @return la entidad {@link TipoEnlace} persistida.
   */
  @Override
  @Transactional
  public TipoEnlace enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, TipoEnlace.class);

    return repository.findById(id).map(tipoEnlace -> {
      if (Boolean.TRUE.equals(tipoEnlace.getActivo())) {
        return tipoEnlace;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoEnlace>> result = validator.validate(
          tipoEnlace,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoEnlace.setActivo(true);
      TipoEnlace returnValue = repository.save(tipoEnlace);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoEnlaceNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoEnlace}.
   *
   * @param id Id del {@link TipoEnlace}.
   * @return la entidad {@link TipoEnlace} persistida.
   */
  @Override
  @Transactional
  public TipoEnlace disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, TipoEnlace.class);

    return repository.findById(id).map(tipoEnlace -> {
      if (Boolean.FALSE.equals(tipoEnlace.getActivo())) {
        return tipoEnlace;
      }

      tipoEnlace.setActivo(false);
      TipoEnlace returnValue = repository.save(tipoEnlace);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoEnlaceNotFoundException(id));
  }

  /**
   * Obtiene todas las entidades {@link TipoEnlace} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  @Override
  public Page<TipoEnlace> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<TipoEnlace> specs = TipoEnlaceSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoEnlace> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene todas las entidades {@link TipoEnlace} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  @Override
  public Page<TipoEnlace> findAllTodos(String query, Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Specification<TipoEnlace> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<TipoEnlace> returnValue = repository.findAll(specs, paging);
    log.debug("findAllTodos(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoEnlace} por id.
   * 
   * @param id Identificador de la entidad {@link TipoEnlace}.
   * @return TipoEnlace la entidad {@link TipoEnlace}.
   */
  @Override
  public TipoEnlace findById(Long id) {
    log.debug("findById(Long id) - start");
    final TipoEnlace returnValue = repository.findById(id).orElseThrow(() -> new TipoEnlaceNotFoundException(id));
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
