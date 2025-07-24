package org.crue.hercules.sgi.csp.service.impl;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoFaseNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.TipoFase;
import org.crue.hercules.sgi.csp.repository.TipoFaseRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoFaseSpecifications;
import org.crue.hercules.sgi.csp.service.TipoFaseService;
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

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Validated
public class TipoFaseServiceImpl implements TipoFaseService {

  private final Validator validator;
  private final TipoFaseRepository tipoFaseRepository;

  /**
   * Guardar {@link TipoFase}.
   *
   * @param tipoFase la entidad {@link TipoFase} a guardar.
   * @return la entidad {@link TipoFase} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public TipoFase create(@Valid TipoFase tipoFase) {
    log.debug("create(TipoFase tipoFase) - start");

    AssertHelper.idIsNull(tipoFase.getId(), TipoFase.class);

    tipoFase.setActivo(Boolean.TRUE);
    TipoFase returnValue = tipoFaseRepository.save(tipoFase);

    log.debug("create(TipoFase tipoFase) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoFase}.
   *
   * @param tipoFaseActualizar la entidad {@link TipoFase} a actualizar.
   * @return la entidad {@link TipoFase} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public TipoFase update(@Valid TipoFase tipoFaseActualizar) {
    log.debug("update(TipoFase tipoFaseActualizar) - start");

    AssertHelper.idNotNull(tipoFaseActualizar.getId(), TipoFase.class);

    return tipoFaseRepository.findById(tipoFaseActualizar.getId()).map(tipoFase -> {
      tipoFase.setNombre(tipoFaseActualizar.getNombre());
      tipoFase.setDescripcion(tipoFaseActualizar.getDescripcion());
      TipoFase returnValue = tipoFaseRepository.save(tipoFase);
      log.debug("update(TipoFase tipoFaseActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFaseNotFoundException(tipoFaseActualizar.getId()));

  }

  /**
   * Obtener todas las entidades {@link TipoFase} activas paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoFase} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoFase> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");

    Specification<TipoFase> specs = TipoFaseSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));
    Page<TipoFase> returnValue = tipoFaseRepository.findAll(specs, pageable);

    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoFase} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoFase} paginadas y/o filtradas.
   */
  @Override
  public Page<TipoFase> findAllTodos(String query, Pageable pageable) {
    log.debug("findAllTodos(String query, Pageable pageable) - start");

    Specification<TipoFase> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoFase> returnValue = tipoFaseRepository.findAll(specs, pageable);

    log.debug("findAllTodos(String query, Pageable pageable) - end");
    return returnValue;

  }

  /**
   * Reactiva el {@link TipoFase}.
   *
   * @param id Id del {@link TipoFase}.
   * @return la entidad {@link TipoFase} persistida.
   */
  @Override
  @Transactional
  public TipoFase enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, TipoFase.class);

    return tipoFaseRepository.findById(id).map(tipoFase -> {
      if (Boolean.TRUE.equals(tipoFase.getActivo())) {
        return tipoFase;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoFase>> result = validator.validate(
          tipoFase,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoFase.setActivo(true);
      TipoFase returnValue = tipoFaseRepository.save(tipoFase);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFaseNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoFase}.
   *
   * @param id Id del {@link TipoFase}.
   * @return la entidad {@link TipoFase} persistida.
   */
  @Override
  @Transactional
  public TipoFase disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, TipoFase.class);

    return tipoFaseRepository.findById(id).map(tipoFase -> {
      if (Boolean.FALSE.equals(tipoFase.getActivo())) {
        return tipoFase;
      }

      tipoFase.setActivo(false);
      TipoFase returnValue = tipoFaseRepository.save(tipoFase);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoFaseNotFoundException(id));
  }

}
