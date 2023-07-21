package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.TipoAmbitoGeograficoNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.repository.TipoAmbitoGeograficoRepository;
import org.crue.hercules.sgi.csp.repository.specification.TipoAmbitoGeograficoSpecifications;
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
 * Service Implementation para la gestión de {@link TipoAmbitoGeografico}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Validated
public class TipoAmbitoGeograficoService {

  private final TipoAmbitoGeograficoRepository repository;
  private final Validator validator;

  /**
   * Obtener todas las entidades {@link TipoAmbitoGeografico} activos paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoAmbitoGeografico} paginadas y/o
   *         filtradas.
   */
  public Page<TipoAmbitoGeografico> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TipoAmbitoGeografico> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<TipoAmbitoGeografico> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TipoAmbitoGeografico} por su id.
   *
   * @param id el id de la entidad {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico}.
   */
  public TipoAmbitoGeografico findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TipoAmbitoGeografico returnValue = repository.findById(id)
        .orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link TipoAmbitoGeografico}.
   *
   * @param tipoAmbitoGeografico la entidad
   *                             {@link TipoAmbitoGeografico} a
   *                             guardar.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  @Transactional
  @Validated({ TipoAmbitoGeografico.OnCrear.class })
  public TipoAmbitoGeografico create(@Valid TipoAmbitoGeografico tipoAmbitoGeografico) {
    log.debug("create(FuenteFinanciacion fuenteFinanciacion) - start");

    AssertHelper.idIsNull(tipoAmbitoGeografico.getId(), TipoAmbitoGeografico.class);

    tipoAmbitoGeografico.setActivo(true);
    TipoAmbitoGeografico returnValue = repository.save(tipoAmbitoGeografico);

    log.debug("create(TipoAmbitoGeografico tipoAmbitoGeografico) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TipoAmbitoGeografico}.
   *
   * @param tipoAmbitoGeografico la entidad
   *                             {@link TipoAmbitoGeografico} a
   *                             actualizar.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  @Transactional
  @Validated({ TipoAmbitoGeografico.OnActualizar.class })
  public TipoAmbitoGeografico update(@Valid TipoAmbitoGeografico tipoAmbitoGeografico) {
    log.debug("update(TipoAmbitoGeografico tipoAmbitoGeografico) - start");

    AssertHelper.idNotNull(tipoAmbitoGeografico.getId(), TipoAmbitoGeografico.class);

    return repository.findById(tipoAmbitoGeografico.getId()).map(fuenteFinanciacionExistente -> {

      // Establecemos los campos actualizables con los recibidos
      fuenteFinanciacionExistente.setNombre(tipoAmbitoGeografico.getNombre());

      // Actualizamos la entidad
      TipoAmbitoGeografico returnValue = repository.save(fuenteFinanciacionExistente);
      log.debug("update(TipoAmbitoGeografico tipoAmbitoGeografico) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(tipoAmbitoGeografico.getId()));
  }

  /**
   * Activa el {@link TipoAmbitoGeografico}.
   *
   * @param id Id del {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  @Transactional
  public TipoAmbitoGeografico activar(Long id) {
    log.debug("activar(Long id) - start");

    AssertHelper.idNotNull(id, TipoAmbitoGeografico.class);

    return repository.findById(id).map(tipoAmbitoGeografico -> {
      if (tipoAmbitoGeografico.getActivo().booleanValue()) {
        // Si esta activo no se hace nada
        return tipoAmbitoGeografico;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<TipoAmbitoGeografico>> result = validator.validate(tipoAmbitoGeografico,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      tipoAmbitoGeografico.setActivo(true);

      TipoAmbitoGeografico returnValue = repository.save(tipoAmbitoGeografico);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(id));
  }

  /**
   * Desactiva el {@link TipoAmbitoGeografico}.
   *
   * @param id Id del {@link TipoAmbitoGeografico}.
   * @return la entidad {@link TipoAmbitoGeografico} persistida.
   */
  @Transactional
  public TipoAmbitoGeografico desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    AssertHelper.idNotNull(id, TipoAmbitoGeografico.class);

    return repository.findById(id).map(tipoAmbitoGeografico -> {
      if (!tipoAmbitoGeografico.getActivo().booleanValue()) {
        // Si no esta activo no se hace nada
        return tipoAmbitoGeografico;
      }

      tipoAmbitoGeografico.setActivo(false);

      TipoAmbitoGeografico returnValue = repository.save(tipoAmbitoGeografico);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoAmbitoGeograficoNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link TipoAmbitoGeografico} activos
   * paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TipoAmbitoGeografico} paginadas
   *         y/o
   *         filtradas.
   */
  public Page<TipoAmbitoGeografico> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<TipoAmbitoGeografico> specs = TipoAmbitoGeograficoSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoAmbitoGeografico> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

}
