package org.crue.hercules.sgi.prc.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.AutorNotFoundException;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.AutorRepository;
import org.crue.hercules.sgi.prc.repository.specification.AutorSpecifications;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaAuthorityHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link Autor}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class AutorService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final AutorRepository repository;
  private final ProduccionCientificaAuthorityHelper authorityHelper;

  public AutorService(
      AutorRepository autorRepository,
      ProduccionCientificaAuthorityHelper authorityHelper) {
    this.repository = autorRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guardar un nuevo {@link Autor}.
   *
   * @param autor la entidad {@link Autor}
   *              a guardar.
   * @return la entidad {@link Autor} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Autor create(@Valid Autor autor) {

    log.debug("create(Autor autor) - start");

    Assert.isNull(autor.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(Autor.class))
            .build());

    Autor returnValue = repository.save(autor);

    log.debug("create(Autor autor) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Autor}.
   *
   * @param autor la entidad {@link Autor}
   *              a actualizar.
   * @return la entidad {@link Autor} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public Autor update(@Valid Autor autor) {
    log.debug("update(Autor autor) - start");

    Assert.notNull(autor.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(Autor.class))
            .build());

    return repository.findById(autor.getId())
        .map(autorExistente -> {

          // Establecemos los autores actualizables con los recibidos
          autorExistente.setFirma(autor.getFirma());
          autorExistente.setPersonaRef(autor.getPersonaRef());
          autorExistente.setNombre(autor.getNombre());
          autorExistente.setApellidos(autor.getApellidos());
          autorExistente.setOrden(autor.getOrden());
          autorExistente.setOrcidId(autor.getOrcidId());
          autorExistente.setFechaInicio(autor.getFechaInicio());
          autorExistente.setFechaFin(autor.getFechaFin());
          autorExistente.setIp(autor.getIp());

          // Actualizamos la entidad
          Autor returnValue = repository.save(autorExistente);
          log.debug("update(Autor autor) - end");
          return returnValue;
        }).orElseThrow(
            () -> new AutorNotFoundException(autor.getId()));
  }

  /**
   * Obtener todas las entidades {@link Autor} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Autor} paginadas y/o
   *         filtradas.
   */
  public Page<Autor> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Autor> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Autor> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Autor} por su id.
   *
   * @param id el id de la entidad {@link Autor}.
   * @return la entidad {@link Autor}.
   */
  public Autor findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Autor returnValue = repository.findById(id)
        .orElseThrow(() -> new AutorNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link Autor}.
   *
   * @param id Id del {@link Autor}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(Autor.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene todos los {@link Autor} por su produccionCientificaId.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @return listado de {@link Autor}.
   */
  public List<Autor> findAllByProduccionCientificaId(Long produccionCientificaId) {
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId)  - start");
    final List<Autor> returnValue = repository.findAllByProduccionCientificaId(produccionCientificaId);
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId)  - end");
    return returnValue;
  }

  /**
   * Obtiene todos los {@link Autor} por su produccionCientificaId
   * paginadas y/o filtradas.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @param query                  la información del filtro.
   * @param pageable               la información de la paginación.
   * @return listado de {@link Autor} paginadas y/o filtradas.
   */
  public Page<Autor> findAllByProduccionCientificaId(Long produccionCientificaId, String query,
      Pageable pageable) {
    log.debug(
        "findAllByProduccionCientificaId(Long produccionCientificaId, String query, Pageable pageable) - start");
    authorityHelper.checkUserHasAuthorityViewProduccionCientifica(produccionCientificaId);
    Specification<Autor> specs = AutorSpecifications.byProduccionCientificaId(produccionCientificaId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    final Page<Autor> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProduccionCientificaId(Long produccionCientificaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Elimina todos los {@link Autor} cuyo produccionCientificaId
   * coincide con el indicado.
   * 
   * @param produccionCientificaId el identificador de la
   *                               {@link ProduccionCientifica}
   * @return el número de registros eliminados
   */
  public int deleteInBulkByProduccionCientificaId(long produccionCientificaId) {
    log.debug("deleteInBulkByProduccionCientificaId(Long produccionCientificaId)  - start");
    final int returnValue = repository.deleteInBulkByProduccionCientificaId(produccionCientificaId);
    log.debug("deleteInBulkByProduccionCientificaId(Long produccionCientificaId)  - end");
    return returnValue;
  }
}
