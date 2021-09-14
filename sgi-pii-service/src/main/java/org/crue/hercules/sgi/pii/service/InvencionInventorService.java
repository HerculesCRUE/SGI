package org.crue.hercules.sgi.pii.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.pii.exceptions.InvencionInventorNotFoundException;
import org.crue.hercules.sgi.pii.exceptions.InvencionNotFoundException;
import org.crue.hercules.sgi.pii.model.Invencion;
import org.crue.hercules.sgi.pii.model.InvencionInventor;
import org.crue.hercules.sgi.pii.repository.InvencionInventorRepository;
import org.crue.hercules.sgi.pii.repository.InvencionRepository;
import org.crue.hercules.sgi.pii.repository.specification.InvencionInventorSpecifications;
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

  public InvencionInventorService(InvencionInventorRepository invencionInventorRepository,
      InvencionRepository invencionRepository) {
    this.repository = invencionInventorRepository;
    this.invencionRepository = invencionRepository;
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

    if (!this.invencionRepository.existsById(invencionId)) {
      throw new InvencionNotFoundException(invencionId);
    }

    final List<Long> invecionInventoresId = invencionInventores.stream().filter(el -> el.getId() != null)
        .map(el -> el.getId()).collect(Collectors.toList());
    final Boolean allInvencionInventorIncluded = invecionInventoresId.isEmpty() ? true
        : this.repository.inventoresBelongsToInvencion(invencionId, invecionInventoresId);
    final Double totalParticipacion = invencionInventores.stream().filter(el -> el.getActivo())
        .mapToDouble(el -> el.getParticipacion().doubleValue()).sum();

    Assert.isTrue(allInvencionInventorIncluded,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.pii.exceptions.NoRelatedEntitiesException.message")
            .parameter("entity", ApplicationContextSupport.getMessage(Invencion.class))
            .parameter("related", ApplicationContextSupport.getMessage(InvencionInventor.class)).build());
    Assert.isTrue(totalParticipacion == 100,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.pii.model.InvencionInventor.participacion.completa")
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionInventor.class)).build());
    invencionInventores.forEach(elem -> {
      commonEntityValidations(elem, true);
    });

    repository
        .deleteInBatch(invencionInventores.stream().filter((elem) -> !elem.getActivo()).collect(Collectors.toList()));

    List<InvencionInventor> returnValue = repository
        .saveAll(invencionInventores.stream().filter((elem) -> elem.getActivo()).collect(Collectors.toList()));

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
   * Desactiva el {@link InvencionInventor}.
   *
   * @param id Id del {@link InvencionInventor}.
   * @return Entidad {@link InvencionInventor} persistida desactivada.
   */
  @Transactional
  public InvencionInventor desactivar(Long id) {
    log.debug("desactivar(Long id) - start");

    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionInventor.class)).build());

    return repository.findById(id).map(invencionInventor -> {
      if (!invencionInventor.getActivo()) {
        // Si no esta activo no se hace nada
        return invencionInventor;
      }

      invencionInventor.setActivo(false);

      InvencionInventor returnValue = repository.save(invencionInventor);
      log.debug("desactivar(Long id) - end");
      return returnValue;
    }).orElseThrow(() -> new InvencionInventorNotFoundException(id));
  }

  private void commonEntityValidations(InvencionInventor invencionInventor, boolean isUpdate) {
    if (!isUpdate) {
      Assert.isNull(invencionInventor.getId(),
          // Defer message resolution untill is needed
          () -> ProblemMessage.builder().key(Assert.class, "isNull")
              .parameter("field",
                  ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.InvencionInventor.id"))
              .parameter("entity", ApplicationContextSupport.getMessage(InvencionInventor.class)).build());
    }
    Assert.notNull(invencionInventor.getInventorRef(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.InvencionInventor.inventorRef"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionInventor.class)).build());

    Assert.notNull(invencionInventor.getParticipacion(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.pii.model.InvencionInventor.participacion"))
            .parameter("entity", ApplicationContextSupport.getMessage(InvencionInventor.class)).build());
  }

}
