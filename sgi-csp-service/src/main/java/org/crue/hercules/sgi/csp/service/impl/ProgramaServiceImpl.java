package org.crue.hercules.sgi.csp.service.impl;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.csp.exceptions.ProgramaNotFoundException;
import org.crue.hercules.sgi.csp.model.BaseActivableEntity;
import org.crue.hercules.sgi.csp.model.BaseEntity;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ProgramaSpecifications;
import org.crue.hercules.sgi.csp.service.ProgramaService;
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
 * Service Implementation para la gestión de {@link Programa}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Validated
public class ProgramaServiceImpl implements ProgramaService {

  private final ProgramaRepository repository;
  private final Validator validator;

  /**
   * Guardar un nuevo {@link Programa}.
   *
   * @param programa la entidad {@link Programa} a guardar.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Programa create(@Valid Programa programa) {
    log.debug("create(Programa programa) - start");

    AssertHelper.idIsNull(programa.getId(), Programa.class);

    if (programa.getPadre() != null) {
      if (programa.getPadre().getId() == null) {
        programa.setPadre(null);
      } else {
        programa.setPadre(repository.findById(programa.getPadre().getId())
            .orElseThrow(() -> new ProgramaNotFoundException(programa.getPadre().getId())));
      }
    }

    programa.setActivo(true);

    Programa returnValue = repository.save(programa);

    log.debug("create(Programa programa) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Programa}.
   *
   * @param programaActualizar la entidad {@link Programa} a actualizar.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public Programa update(@Valid Programa programaActualizar) {
    log.debug("update(Programa programaActualizar) - start");

    AssertHelper.idNotNull(programaActualizar.getId(), Programa.class);

    if (programaActualizar.getPadre() != null) {
      if (programaActualizar.getPadre().getId() == null) {
        programaActualizar.setPadre(null);
      } else {
        programaActualizar.setPadre(repository.findById(programaActualizar.getPadre().getId())
            .orElseThrow(() -> new ProgramaNotFoundException(programaActualizar.getPadre().getId())));
      }
    }

    return repository.findById(programaActualizar.getId()).map(programa -> {
      programa.setNombre(programaActualizar.getNombre());
      programa.setDescripcion(programaActualizar.getDescripcion());
      programa.setPadre(programaActualizar.getPadre());

      Programa returnValue = repository.save(programa);
      log.debug("update(Programa programaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProgramaNotFoundException(programaActualizar.getId()));
  }

  /**
   * Reactiva el {@link Programa}.
   *
   * @param id Id del {@link Programa}.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa enable(Long id) {
    log.debug("enable(Long id) - start");

    AssertHelper.idNotNull(id, Programa.class);

    return repository.findById(id).map(programa -> {
      if (programa.getActivo().booleanValue()) {
        // Si esta activo no se hace nada
        return programa;
      }

      // Invocar validaciones asociadas a OnActivar
      Set<ConstraintViolation<Programa>> result = validator.validate(
          programa,
          BaseActivableEntity.OnActivar.class);
      if (!result.isEmpty()) {
        throw new ConstraintViolationException(result);
      }

      programa.setActivo(true);

      Programa returnValue = repository.save(programa);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProgramaNotFoundException(id));
  }

  /**
   * Desactiva el {@link Programa}.
   *
   * @param id Id del {@link Programa}.
   * @return la entidad {@link Programa} persistida.
   */
  @Override
  @Transactional
  public Programa disable(Long id) {
    log.debug("disable(Long id) - start");

    AssertHelper.idNotNull(id, Programa.class);

    return repository.findById(id).map(programa -> {
      if (!programa.getActivo().booleanValue()) {
        // Si no esta activo no se hace nada
        return programa;
      }

      programa.setActivo(false);
      Programa returnValue = repository.save(programa);
      log.debug("disable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new ProgramaNotFoundException(id));
  }

  /**
   * Obtiene {@link Programa} por su id.
   *
   * @param id el id de la entidad {@link Programa}.
   * @return la entidad {@link Programa}.
   */
  @Override
  public Programa findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Programa returnValue = repository.findById(id).orElseThrow(() -> new ProgramaNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link Programa} activos.
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  @Override
  public Page<Programa> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Programa> specs = ProgramaSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los planes activos (los {@link Programa} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  @Override
  public Page<Programa> findAllPlan(String query, Pageable pageable) {
    log.debug("findAllPlan(String query, Pageable pageable) - start");
    Specification<Programa> specs = ProgramaSpecifications.activos().and(ProgramaSpecifications.planes())
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllPlan(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los planes (los {@link Programa} con padre null).
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  @Override
  public Page<Programa> findAllTodosPlan(String query, Pageable pageable) {
    log.debug("findAllTodosPlan(String query, Pageable pageable) - start");
    Specification<Programa> specs = ProgramaSpecifications.planes().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllTodosPlan(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link Programa} hijos directos del {@link Programa} con el id
   * indicado.
   *
   * @param programaId el id de la entidad {@link Programa}.
   * @param query      la información del filtro.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas.
   */
  @Override
  public Page<Programa> findAllHijosPrograma(Long programaId, String query, Pageable pageable) {
    log.debug("findAllHijosPrograma(Long programaId, String query, Pageable pageable) - start");
    Specification<Programa> specs = ProgramaSpecifications.hijos(programaId)
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Programa> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllHijosPrograma(Long programaId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
