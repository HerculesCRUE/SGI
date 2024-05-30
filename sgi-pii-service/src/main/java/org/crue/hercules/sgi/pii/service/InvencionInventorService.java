package org.crue.hercules.sgi.pii.service;

import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_NOTNULL;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_ENTITY;
import static org.crue.hercules.sgi.pii.util.AssertHelper.PROBLEM_MESSAGE_PARAMETER_FIELD;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InvencionInventorNoDeletableException;
import org.crue.hercules.sgi.pii.exceptions.InvencionInventorNotFoundException;
import org.crue.hercules.sgi.pii.exceptions.InvencionNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.repository.InvencionInventorRepository;
import org.crue.hercules.sgi.pii.repository.InvencionRepository;
import org.crue.hercules.sgi.pii.repository.RepartoEquipoInventorRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionInventorSpecifications;
import org.crue.hercules.sgi.pii.repository.specification.RepartoEquipoInventorSpecifications;
import org.crue.hercules.sgi.pii.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar las entidades {@link InvencionInventor}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class InvencionInventorService {

  private final InvencionInventorRepository repository;
  private final InvencionRepository invencionRepository;
  private final RepartoEquipoInventorRepository repartoEquipoInventorRepository;

  public InvencionInventorService(InvencionInventorRepository invencionInventorRepository,
      InvencionRepository invencionRepository,
      RepartoEquipoInventorRepository repartoEquipoInventorRepository) {
    this.repository = invencionInventorRepository;
    this.invencionRepository = invencionRepository;
    this.repartoEquipoInventorRepository = repartoEquipoInventorRepository;
  }

  /**
   * Obtener todas las entidades {@link InvencionInventor} activos paginadas y/o
   * filtradas.
   *
   * @param pageable información de la paginación.
   * @param query    información del filtro.
   * @return la lista de entidades {@link InvencionInventor} paginadas y/o
   *         filtradas.
   */
  public Page<InvencionInventor> findActive(String query, Pageable pageable) {
    log.debug("findActive(String query, Pageable pageable) - start");

    Specification<InvencionInventor> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<InvencionInventor> returnValue = repository.findAll(specs, pageable);
    log.debug("findActive(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link InvencionInventor} relacionadas a la
   * {@link Invencion} pasada por parámetros, que estén activas, paginadas y/o
   * filtradas.
   * 
   * @param invencionId Id de la {@link Invencion}
   * @param pageable    información de la paginación.
   * @param query       información del filtro.
   * @return la lista de entidades {@link InvencionInventor} paginadas y/o
   *         filtradas.
   */
  public Page<InvencionInventor> findActivosByInvencion(Long invencionId, String query, Pageable pageable) {
    log.debug("findActiveByInvencion(Invencion invencionId, String query, Pageable pageable) - start");

    if (!this.invencionRepository.existsById(invencionId)) {
      throw new InvencionNotFoundException(invencionId);
    }

    Specification<InvencionInventor> specs = InvencionInventorSpecifications.invencionById(invencionId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<InvencionInventor> returnValue = repository.findAll(specs, pageable);

    log.debug("findActiveByInvencion(Invencion invencionId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link InvencionInventor} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link InvencionInventor} paginadas y/o
   *         filtradas.
   */
  public Page<InvencionInventor> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");

    Specification<InvencionInventor> specs = SgiRSQLJPASupport.toSpecification(query);
    Page<InvencionInventor> returnValue = repository.findAll(specs, pageable);

    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link InvencionInventor} por su id.
   *
   * @param id el id de la entidad {@link InvencionInventor}.
   * @return la entidad {@link InvencionInventor}.
   */
  public InvencionInventor findById(Long id) {
    log.debug("findById(Long id)  - start");

    final InvencionInventor returnValue = repository.findById(id)
        .orElseThrow(() -> new InvencionInventorNotFoundException(id));

    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Guardar un nuevo {@link InvencionInventor}.
   *
   * @param invencionInventor la entidad {@link InvencionInventor} a guardar.
   * @return la entidad {@link InvencionInventor} persistida.
   */
  @Transactional
  public InvencionInventor create(InvencionInventor invencionInventor) {
    log.debug("create(InvencionInventor invencionInventor) - start");

    commonEntityValidations(invencionInventor, false);
    InvencionInventor returnValue = repository.save(invencionInventor);

    log.debug("create(InvencionInventor invencionInventor) - end");
    return returnValue;
  }

  /**
   * Guarda, actualiza o elimina los {@link InvencionInventor} pasados por
   * parámetros. Se realiza de forma transaccional de manera que si falla la
   * operación en algún elemento, se revierte la operación completa, respetando de
   * esta forma el criterio previamente validado relativo a la suma de la
   * participación de los {@link InvencionInventor}
   *
   * @param invencionInventores las entidades {@link InvencionInventor} a
   *                            modificar/crear/eliminar.
   * @param invencionId         Id de la {@link Invencion}
   * @return las entidades {@link InvencionInventor} persistidas.
   */
  @Transactional
  public List<InvencionInventor> saveUpdateOrDeleteBatchMode(Long invencionId,
      List<InvencionInventor> invencionInventores) {
    log.debug("saveUpdateOrDeleteBatchMode(Long invencionId, List<InvencionInventor> invencionInventores) - start");

    AssertHelper.idNotNull(invencionId, InvencionInventor.class);

    if (!this.invencionRepository.existsById(invencionId)) {
      throw new InvencionNotFoundException(invencionId);
    }

    final List<Long> invecionInventoresId = invencionInventores.stream().filter(el -> el.getId() != null)
        .map(InvencionInventor::getId).collect(Collectors.toList());
    final Boolean allInvencionInventorIncluded = invecionInventoresId.isEmpty() ? Boolean.TRUE
        : this.repository.inventoresBelongsToInvencion(invencionId, invecionInventoresId);
    final Double totalParticipacion = invencionInventores.stream()
        .mapToDouble(el -> el.getParticipacion().doubleValue()).sum();

    Assert.isTrue(allInvencionInventorIncluded,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.pii.exceptions.NoRelatedEntitiesException.message")
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Invencion.class))
            .parameter("related", ApplicationContextSupport.getMessage(InvencionInventor.class)).build());
    Assert.isTrue(totalParticipacion == 100,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.pii.model.InvencionInventor.participacion.completa")
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(InvencionInventor.class))
            .build());
    invencionInventores.forEach(elem -> commonEntityValidations(elem, true));

    List<InvencionInventor> inventoresBD = repository.findByInvencionId(invencionId);

    List<InvencionInventor> inventoresEliminar = inventoresBD.stream()
        .filter(inventor -> invencionInventores.stream().map(InvencionInventor::getId)
            .noneMatch(id -> Objects.equals(id, inventor.getId())))
        .collect(Collectors.toList());

    if (!inventoresEliminar.isEmpty()) {
      inventoresEliminar.forEach(inventor -> {
        if (repartoEquipoInventorRepository
            .count(RepartoEquipoInventorSpecifications.byInvencionInventorId(inventor.getId())) > 0) {
          throw new InvencionInventorNoDeletableException(inventor.getId());
        }

        this.repository.deleteById(inventor.getId());
      });
    }

    List<InvencionInventor> returnValue = repository.saveAll(invencionInventores);

    log.debug("saveUpdateOrDeleteBatchMode(Long invencionId, List<InvencionInventor> invencionInventores) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link InvencionInventor}.
   *
   * @param invencionInventor la entidad {@link InvencionInventor} a actualizar.
   * @return la entidad {@link InvencionInventor} persistida.
   */
  @Transactional
  public InvencionInventor update(@Valid InvencionInventor invencionInventor) {
    log.debug("update(@Valid InvencionInventor invencionInventor) - start");

    commonEntityValidations(invencionInventor, true);

    return repository.findById(invencionInventor.getId()).map(invencionInventorExistente -> {

      invencionInventorExistente.setParticipacion(invencionInventor.getParticipacion());
      invencionInventorExistente.setRepartoUniversidad(invencionInventor.getRepartoUniversidad());
      InvencionInventor returnValue = repository.save(invencionInventorExistente);

      log.debug("update(@Valid InvencionInventor invencionInventor) - end");
      return returnValue;
    }).orElseThrow(() -> new InvencionInventorNotFoundException(invencionInventor.getId()));
  }

  /**
   * Comprueba la existencia de la {@link InvencionInventor} por id.
   *
   * @param id el id de la entidad {@link InvencionInventor}.
   * @return true si existe y false en caso contrario.
   */
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);

    final boolean existe = repository.existsById(id);

    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Elimina el {@link InvencionInventor}.
   *
   * @param id Id del {@link InvencionInventor}.
   * 
   * @return true si es eliminable, false en caso contrario
   */
  @Transactional
  public boolean deletable(Long id) {
    log.debug("deletable(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(InvencionInventor.class))
            .build());

    if (!this.repository.existsById(id)) {
      throw new InvencionInventorNotFoundException(id);
    }

    boolean deletable = repartoEquipoInventorRepository
        .count(RepartoEquipoInventorSpecifications.byInvencionInventorId(id)) == 0;

    log.debug("deletable(Long id) - end");
    return deletable;
  }

  private void commonEntityValidations(InvencionInventor invencionInventor, boolean isUpdate) {
    if (!isUpdate) {
      Assert.isNull(invencionInventor.getId(),
          // Defer message resolution untill is needed
          () -> ProblemMessage.builder().key(Assert.class, "isNull")
              .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                  ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.InvencionInventor.id"))
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                  ApplicationContextSupport.getMessage(InvencionInventor.class))
              .build());
    }
    Assert.notNull(invencionInventor.getInventorRef(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.InvencionInventor.inventorRef"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(InvencionInventor.class))
            .build());

    Assert.notNull(invencionInventor.getParticipacion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD,
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.InvencionInventor.participacion"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(InvencionInventor.class))
            .build());
  }

  public List<InvencionInventor> findByInvencionId(Long invencionId) {
    return repository.findByInvencionId(invencionId);
  }

}
