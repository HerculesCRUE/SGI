package org.crue.hercules.sgi.csp.service.impl;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoHitoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.repository.TipoHitoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoHitoSpecifications;
import org.crue.hercules.sgi.csp.service.TipoHitoService;
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
public class TipoHitoServiceImpl implements TipoHitoService {

  private final TipoHitoRepository tipoHitoRepository;
  private final Validator validator;

  /**
   * Guardar {@link TipoHito}.
   *
   * @param tipoHito la entidad {@link TipoHito} a guardar.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public TipoHito create(@Valid TipoHito tipoHito) {
    log.debug("create(TipoHito tipoHito) - start");

    AssertHelper.idIsNull(tipoHito.getId(), TipoHito.class);

    tipoHito.setActivo(Boolean.TRUE);
    TipoHito returnValue = tipoHitoRepository.save(tipoHito);

    log.debug("create(TipoHito tipoHito) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoHito}.
   *
   * @param tipoHitoActualizar la entidad {@link TipoHito} a actualizar.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public TipoHito update(@Valid TipoHito tipoHitoActualizar) {
    log.debug("update(TipoHito tipoHitoActualizar) - start");

    AssertHelper.idNotNull(tipoHitoActualizar.getId(), TipoHito.class);

    return tipoHitoRepository.findById(tipoHitoActualizar.getId()).map(tipoHito -> {
      tipoHito.setNombre(tipoHitoActualizar.getNombre());
      tipoHito.setDescripcion(tipoHitoActualizar.getDescripcion());

      TipoHito returnValue = tipoHitoRepository.save(tipoHito);
      log.debug("update(TipoHito tipoHitoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoHitoNotFoundException(tipoHitoActualizar.getId()));

  }

  /**
   * Obtener todas las entidades {@link TipoHito} activas paginadas
   *
   * @param pageable la información de la paginación.
   * @param query    información del filtro.
   * @return la lista de entidades {@link TipoHito} paginadas
   */
  @Override
  public Page<TipoHito> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoHito> specs = TipoHitoSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoHito> returnValue = tipoHitoRepository.findAll(specs, pageable);

    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link TipoHito} paginadas
   *
   * @param pageable la información de la paginación.
   * @param query    información del filtro.
   * @return la lista de entidades {@link TipoHito} paginadas
   */
  @Override
  public Page<TipoHito> findAllTodos(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoHito> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoHito> returnValue = tipoHitoRepository.findAll(specs, pageable);

    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoHito} por id.
   *
   * @param id el id de la entidad {@link TipoHito}.
   * @return la entidad {@link TipoHito}.
   */
  @Override
  public TipoHito findById(Long id) throws TipoHitoNotFoundException {
    log.debug("findById(Long id) id:{}- start", id);
    TipoHito tipoHito = tipoHitoRepository.findById(id).orElseThrow(() -> new TipoHitoNotFoundException(id));
    log.debug("findById(Long id) id:{}- end", id);
    return tipoHito;
  }

  /**
   * Reactiva el {@link TipoHito}.
   *
   * @param id Id del {@link TipoHito}.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  public TipoHito enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, TipoHito.class);

    return tipoHitoRepository.findById(id).map(tipoHito -> {
      if (Boolean.TRUE.equals(tipoHito.getActivo())) {
        return tipoHito;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoHito>> result = validator.validate(
          tipoHito,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoHito.setActivo(true);
      TipoHito returnValue = tipoHitoRepository.save(tipoHito);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoHitoNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoHito}.
   *
   * @param id Id del {@link TipoHito}.
   * @return la entidad {@link TipoHito} persistida.
   */
  @Override
  @Transactional
  public TipoHito disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, TipoHito.class);

    return tipoHitoRepository.findById(id).map(tipoHito -> {
      if (Boolean.FALSE.equals(tipoHito.getActivo())) {
        return tipoHito;
      }

      tipoHito.setActivo(false);
      TipoHito returnValue = tipoHitoRepository.save(tipoHito);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoHitoNotFoundException(id));
  }

}