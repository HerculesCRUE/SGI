package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.MESSAGE_KEY_ID;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_ISNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.config.SgiConfigProperties;
import org.crue.hercules.sgi.pii.dto.InvencionDto;
import org.crue.hercules.sgi.pii.exceptions.InvencionNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.TipoProteccion;
import org.crue.hercules.sgi.pii.repository.InvencionRepository;
import org.crue.hercules.sgi.pii.repository.predicate.InvencionPredicateResolver;
import org.crue.hercules.sgi.pii.repository.specification.InvencionSpecifications;
import org.crue.hercules.sgi.pii.util.PeriodDateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link Invencion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class InvencionService {

  private final Validator validator;
  private final SgiConfigProperties sgiConfigProperties;
  private final InvencionRepository repository;

  /**
   * Obtener todas las entidades {@link Invencion} activos paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  public Page<Invencion> findActivos(String query, Pageable pageable) {
    log.debug("findActivos(String query, Pageable pageable) - start");
    Specification<Invencion> specs = InvencionSpecifications
        .distinct().and(InvencionSpecifications.activos()
            .and(SgiRSQLJPASupport.toSpecification(query, InvencionPredicateResolver.getInstance())));

    Page<Invencion> returnValue = repository.findAll(specs, pageable);
    log.debug("findActivos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link Invencion} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Invencion} paginadas y/o filtradas.
   */
  public Page<Invencion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Invencion> specs = InvencionSpecifications.distinct()
        .and(SgiRSQLJPASupport.toSpecification(query, InvencionPredicateResolver.getInstance()));

    Page<Invencion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Invencion} por su id.
   *
   * @param id el id de la entidad {@link Invencion}.
   * @return la entidad {@link Invencion}.
   */
  public Invencion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Invencion returnValue = repository.findById(id).orElseThrow(() -> new InvencionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link Invencion}.
   *
   * @param invencion la entidad {@link Invencion} a guardar.
   * @return la entidad {@link Invencion} persistida.
   */
  @Transactional
  @Validated({ Invencion.OnCrear.class })
  public Invencion create(@Valid Invencion invencion) {
    log.debug("create(Invencion invencion) - start");

    Assert.isNull(invencion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());
    Assert.notNull(invencion.getTipoProteccion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(TipoProteccion.class))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());
    Assert.notNull(invencion.getTipoProteccion().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(TipoProteccion.class))
            .build());

    invencion.setActivo(true);
    Invencion returnValue = repository.save(invencion);

    log.debug("create(Invencion invencion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Invencion}.
   *
   * @param invencion la entidad {@link Invencion} a actualizar.
   * @return la entidad {@link Invencion} persistida.
   */
  @Transactional
  @Validated({ Invencion.OnActualizar.class })
  public Invencion update(@Valid Invencion invencion) {
    log.debug("update(Invencion invencion) - start");

    Assert.notNull(invencion.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());
    Assert.notNull(invencion.getTipoProteccion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(TipoProteccion.class))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());
    Assert.notNull(invencion.getTipoProteccion().getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(TipoProteccion.class))
            .build());

    return repository.findById(invencion.getId()).map(invencionExistente -> {

      if (!invencionExistente.getTipoProteccion().getId().equals(invencion.getTipoProteccion().getId())) {
        // Si estamos mofificando el TipoProteccion invocar validaciones asociadas
        // a OnActualizarTipoProteccion
        Set<ConstraintViolation<Invencion>> result = validator.validate(invencion,
            Invencion.OnActualizarTipoProteccion.class);
        if (!result.isEmpty()) {
          throw new ConstraintViolationException(result);
        }
      }

      // Establecemos los campos actualizables con los recibidos
      invencionExistente.setTitulo(invencion.getTitulo());
      invencionExistente.setFechaComunicacion(invencion.getFechaComunicacion());
      invencionExistente.setDescripcion(invencion.getDescripcion());
      invencionExistente.setComentarios(invencion.getComentarios());
      invencionExistente.setProyectoRef(invencion.getProyectoRef());
      invencionExistente.setTipoProteccion(invencion.getTipoProteccion());

      // Actualizamos la entidad
      Invencion returnValue = repository.save(invencionExistente);
      log.debug("update(Invencion invencion) - end");
      return returnValue;
    }).orElseThrow(() -> new InvencionNotFoundException(invencion.getId()));
  }

  /**
   * Activa la {@link Invencion}.
   *
   * @param id Id de la {@link Invencion}.
   * @return la entidad {@link Invencion} persistida.
   */
  @Transactional
  public Invencion activar(Long id) {
    log.debug("activar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());

    return repository.findById(id).map(invencion -> {
      if (Boolean.TRUE.equals(invencion.getActivo())) {
        // Si esta activo no se hace nada
        return invencion;
      }

      invencion.setActivo(true);

      Invencion returnValue = repository.save(invencion);
      log.debug("enable(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new InvencionNotFoundException(id));
  }

  /**
   * Desactiva la {@link Invencion}.
   *
   * @param id Id de la {@link Invencion}.
   * @return la entidad {@link Invencion} persistida.
   */
  @Transactional
  public Invencion desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .build());

    return repository.findById(id).map(invencion -> {
      if (Boolean.FALSE.equals(invencion.getActivo())) {
        // Si no esta activo no se hace nada
        return invencion;
      }

      invencion.setActivo(false);

      Invencion returnValue = repository.save(invencion);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new InvencionNotFoundException(id));
  }

  /**
   * Comprueba la existencia de la {@link Invencion} por id.
   *
   * @param id el id de la entidad {@link Invencion}.
   * @return true si existe y false en caso contrario.
   */
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  public List<InvencionDto> findInvencionesProduccionCientifica(Integer anioInicio, Integer anioFin,
      String universidadId) {

    Instant fechaInicioBaremacion = PeriodDateUtil.calculateFechaInicioBaremacionByAnio(
        anioInicio, sgiConfigProperties.getTimeZone());

    Instant fechaFinBaremacion = PeriodDateUtil.calculateFechaFinBaremacionByAnio(
        anioFin, sgiConfigProperties.getTimeZone());

    return repository.findInvencionesProduccionCientifica(fechaInicioBaremacion, fechaFinBaremacion, universidadId);
  }

  /**
   * Obtiene los ids de {@link Invencion} modificados que esten activos y que
   * cumplan
   * las condiciones indicadas en el filtro de búsqueda
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Invencion}.
   */
  public List<Long> findIdsInvencionesModificadas(String query) {
    log.debug("findIdsInvencionesModificadas(String query) - start");

    Specification<Invencion> specs = InvencionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query, InvencionPredicateResolver.getInstance()));

    List<Long> returnValue = repository.findIds(specs);

    log.debug("findIdsInvencionesModificadas(String query) - end");

    return returnValue;
  }

}
