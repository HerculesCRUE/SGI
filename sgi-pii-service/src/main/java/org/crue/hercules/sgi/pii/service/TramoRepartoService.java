package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.TramoRepartoNotFoundException;
import org.crue.hercules.sgi.pii.model.TramoReparto;
import org.crue.hercules.sgi.pii.model.TramoReparto.Tipo;
import org.crue.hercules.sgi.pii.repository.TramoRepartoRepository;
import org.crue.hercules.sgi.pii.repository.predicate.TramoRepartoPredicateResolver;
import org.crue.hercules.sgi.pii.repository.specification.TramoRepartoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link TramoReparto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class TramoRepartoService {

  private final TramoRepartoRepository repository;

  public TramoRepartoService(TramoRepartoRepository tramoRepartoRepository) {
    this.repository = tramoRepartoRepository;
  }

  /**
   * Obtener todas las entidades {@link TramoReparto} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link TramoReparto} paginadas y/o filtradas.
   */
  public Page<TramoReparto> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<TramoReparto> specs = SgiRSQLJPASupport.toSpecification(query,
        TramoRepartoPredicateResolver.getInstance());

    Page<TramoReparto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link TramoReparto} por su id.
   *
   * @param id el id de la entidad {@link TramoReparto}.
   * @return la entidad {@link TramoReparto}.
   */
  public TramoReparto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final TramoReparto returnValue = repository.findById(id).orElseThrow(() -> new TramoRepartoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link TramoReparto}.
   *
   * @param tramoReparto la entidad {@link TramoReparto} a guardar.
   * @return la entidad {@link TramoReparto} persistida.
   */
  @Transactional
  @Validated({ TramoReparto.OnCrear.class })
  public TramoReparto create(@Valid TramoReparto tramoReparto) {
    log.debug("create(TramoReparto tramoReparto) - start");

    Assert.isNull(tramoReparto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "isNull")
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(TramoReparto.class))
            .build());

    TramoReparto returnValue = repository.save(tramoReparto);

    log.debug("create(TramoReparto tramoReparto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link TramoReparto}.
   *
   * @param tramoReparto la entidad {@link TramoReparto} a actualizar.
   * @return la entidad {@link TramoReparto} persistida.
   */
  @Transactional
  @Validated({ TramoReparto.OnActualizar.class })
  public TramoReparto update(@Valid TramoReparto tramoReparto) {
    log.debug("update(TramoReparto tramoReparto) - start");

    Assert.notNull(tramoReparto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(TramoReparto.class))
            .build());

    return repository.findById(tramoReparto.getId()).map(tramorepartoExistente -> {

      // Establecemos los campos actualizables con los recibidos
      tramorepartoExistente.setDesde(tramoReparto.getDesde());
      tramorepartoExistente.setHasta(tramoReparto.getHasta());
      tramorepartoExistente.setPorcentajeUniversidad(tramoReparto.getPorcentajeUniversidad());
      tramorepartoExistente.setPorcentajeInventores(tramoReparto.getPorcentajeInventores());
      tramorepartoExistente.setTipo(tramoReparto.getTipo());

      // Actualizamos la entidad
      TramoReparto returnValue = repository.save(tramorepartoExistente);
      log.debug("update(TramoReparto tramoReparto) - end");
      return returnValue;
    }).orElseThrow(() -> new TramoRepartoNotFoundException(tramoReparto.getId()));
  }

  /**
   * Elimina el {@link TramoReparto}.
   *
   * @param id Id del {@link TramoReparto}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(TramoReparto.class))
            .build());
    if (!repository.existsById(id)) {
      throw new TramoRepartoNotFoundException(id);
    }
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Comprueba si existe un {@link TramoReparto} activo del tipo indicado.
   * 
   * @param tipo {@link TramoReparto.Tipo} indicado.
   * @return boolean que indica si existe o no.
   */
  public boolean existTipoTramoReparto(Tipo tipo) {
    log.debug("hasTipoTramoReparto(Tipo tipo) - start");
    Assert.notNull(tipo,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("tipo"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(TramoReparto.class))
            .build());
    final boolean returnValue = this.repository.count(TramoRepartoSpecifications.withTipo(tipo)) > 0;
    log.debug("hasTipoTramoReparto(Tipo tipo) - end");
    return returnValue;
  }

  /**
   * Comprueba si el tramo de {@link TramoReparto} se puede eliminar o editar su
   * rango
   * 
   * @param id del {@link TramoReparto} indicado.
   * @return boolean que indica si existe o no.
   */
  public boolean isTramoRepartoModificable(Long id) {
    log.debug("isTramoRepartoModificable(Long id) - start");
    final boolean returnValue = this.repository.count(TramoRepartoSpecifications.hasTramoRepartoGreatestDesde(id)) > 0;
    log.debug("isTramoRepartoModificable(Long id) - end");
    return returnValue;
  }
}
