package org.crue.hercules.sgi.csp.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.RolSocioNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.repository.RolSocioRepository;
import org.crue.hercules.sgi.csp.repository.specification.RolSocioSpecifications;
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
 * Service para gestionar {@link RolSocio}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class RolSocioService {

  private final Validator validator;
  private final RolSocioRepository repository;

  /**
   * Guardar un nuevo {@link RolSocio}.
   *
   * @param rolSocio la entidad {@link RolSocio} a
   *                 guardar.
   * @return la entidad {@link RolSocio} persistida.
   */
  @Transactional
  @Validated({ RolSocio.OnCrear.class })
  public RolSocio create(@Valid RolSocio rolSocio) {
    log.debug("create(RolSocio rolSocio) - start");

    AssertHelper.idIsNull(rolSocio.getId(), RolSocio.class);

    rolSocio.setActivo(true);
    RolSocio returnValue = repository.save(rolSocio);

    log.debug("create(RolSocio rolSocio) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link RolSocio}.
   *
   * @param rolSocio la entidad {@link RolSocio} a
   *                 actualizar.
   * @return la entidad {@link RolSocio} persistida.
   */
  @Transactional
  @Validated({ RolSocio.OnActualizar.class })
  public RolSocio update(@Valid RolSocio rolSocio) {
    log.debug("update(RolSocio rolSocio) - start");

    AssertHelper.idNotNull(rolSocio.getId(), RolSocio.class);

    return repository.findById(rolSocio.getId()).map(rolSocioExistente -> {

      // Establecemos los campos actualizables con los recibidos
      rolSocioExistente.setNombre(rolSocio.getNombre());
      rolSocioExistente.setDescripcion(rolSocio.getDescripcion());
      rolSocioExistente.setAbreviatura(rolSocio.getAbreviatura());
      rolSocioExistente.setCoordinador(rolSocio.getCoordinador());

      // Actualizamos la entidad
      RolSocio returnValue = repository.save(rolSocioExistente);
      log.debug("update(RolSocio rolSocio) - end");
      return returnValue;
    }).orElseThrow(() -> new RolSocioNotFoundException(rolSocio.getId()));
  }

  /**
   * Activa el {@link RolSocio}.
   *
   * @param id Id del {@link RolSocio}.
   * @return la entidad {@link RolSocio} persistida.
   */
  @Transactional
  public RolSocio activar(Long id) {
    log.debug("activar(Long id) - start");

    AssertHelper.idNotNull(id, RolSocio.class);

    return repository.findById(id).map(rolSocio -> {
      if (Boolean.TRUE.equals(rolSocio.getActivo())) {
        // Si esta activo no se hace nada
        return rolSocio;
      }
      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<RolSocio>> result = validator.validate(rolSocio,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      rolSocio.setActivo(true);

      RolSocio returnValue = repository.save(rolSocio);
      log.debug("activar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RolSocioNotFoundException(id));
  }

  /**
   * Desactiva el {@link RolSocio}.
   *
   * @param id Id del {@link RolSocio}.
   * @return la entidad {@link RolSocio} persistida.
   */
  @Transactional
  public RolSocio desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    AssertHelper.idNotNull(id, RolSocio.class);

    return repository.findById(id).map(rolSocio -> {
      if (!rolSocio.getActivo()) {
        // Si no esta activo no se hace nada
        return rolSocio;
      }

      rolSocio.setActivo(false);

      RolSocio returnValue = repository.save(rolSocio);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new RolSocioNotFoundException(id));
  }

  /**
   * Obtener todas las entidades {@link RolSocio} activos paginadas
   * y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link RolSocio} paginadas y/o
   *         filtradas.
   */
  public Page<RolSocio> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<RolSocio> specs = RolSocioSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<RolSocio> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link RolSocio} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link RolSocio} paginadas y/o
   *         filtradas.
   */
  public Page<RolSocio> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<RolSocio> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<RolSocio> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link RolSocio} por su id.
   *
   * @param id el id de la entidad {@link RolSocio}.
   * @return la entidad {@link RolSocio}.
   */
  public RolSocio findById(Long id) {
    log.debug("findById(Long id)  - start");
    final RolSocio returnValue = repository.findById(id)
        .orElseThrow(() -> new RolSocioNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

}