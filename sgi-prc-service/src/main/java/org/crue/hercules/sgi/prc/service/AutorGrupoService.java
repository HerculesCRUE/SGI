package org.crue.hercules.sgi.prc.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.AutorGrupoNotFoundException;
import org.crue.hercules.sgi.prc.model.Autor;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.AutorGrupoRepository;
import org.crue.hercules.sgi.prc.repository.specification.AutorGrupoSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link AutorGrupo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class AutorGrupoService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final AutorGrupoRepository repository;

  public AutorGrupoService(
      AutorGrupoRepository autorGrupoRepository) {
    this.repository = autorGrupoRepository;
  }

  /**
   * Guardar un nuevo {@link AutorGrupo}.
   *
   * @param autorGrupo la entidad {@link AutorGrupo}
   *                   a guardar.
   * @return la entidad {@link AutorGrupo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public AutorGrupo create(@Valid AutorGrupo autorGrupo) {

    log.debug("create(AutorGrupo autorGrupo) - start");

    Assert.isNull(autorGrupo.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(AutorGrupo.class))
            .build());

    AutorGrupo returnValue = repository.save(autorGrupo);

    log.debug("create(AutorGrupo autorGrupo) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link AutorGrupo}.
   *
   * @param autorGrupo la entidad {@link AutorGrupo}
   *                   a actualizar.
   * @return la entidad {@link AutorGrupo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public AutorGrupo update(@Valid AutorGrupo autorGrupo) {
    log.debug("update(AutorGrupo autorGrupo) - start");

    Assert.notNull(autorGrupo.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(AutorGrupo.class))
            .build());

    return repository.findById(autorGrupo.getId())
        .map(autorGrupoExistente -> {

          // Establecemos los autoresGrupos actualizables con los recibidos
          autorGrupoExistente.setEstado(autorGrupo.getEstado());
          autorGrupoExistente.setGrupoRef(autorGrupo.getGrupoRef());

          // Actualizamos la entidad
          AutorGrupo returnValue = repository.save(autorGrupoExistente);
          log.debug("update(AutorGrupo autorGrupo) - end");
          return returnValue;
        }).orElseThrow(
            () -> new AutorGrupoNotFoundException(autorGrupo.getId()));
  }

  /**
   * Obtener todas las entidades {@link AutorGrupo} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link AutorGrupo} paginadas y/o
   *         filtradas.
   */
  public Page<AutorGrupo> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<AutorGrupo> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<AutorGrupo> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link AutorGrupo} por su id.
   *
   * @param id el id de la entidad {@link AutorGrupo}.
   * @return la entidad {@link AutorGrupo}.
   */
  public AutorGrupo findById(Long id) {
    log.debug("findById(Long id)  - start");
    final AutorGrupo returnValue = repository.findById(id)
        .orElseThrow(() -> new AutorGrupoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link AutorGrupo}.
   *
   * @param id Id del {@link AutorGrupo}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(AutorGrupo.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene todos los {@link AutorGrupo} por su autorId.
   *
   * @param autorId el id de {@link Autor}.
   * @return listado de {@link AutorGrupo}.
   */
  public List<AutorGrupo> findAllByAutorId(Long autorId) {
    log.debug("findAllByAutorId(Long autorId)  - start");
    final List<AutorGrupo> returnValue = repository.findAllByAutorId(autorId);
    log.debug("findAllByAutorId(Long autorId)  - end");
    return returnValue;
  }

  /**
   * Obtiene todos los {@link AutorGrupo} por su autorId
   * paginadas y/o filtradas.
   *
   * @param autorId  el id de {@link Autor}.
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return listado de {@link AutorGrupo} paginadas y/o filtradas.
   */
  public Page<AutorGrupo> findAllByAutorId(Long autorId, String query,
      Pageable pageable) {
    log.debug(
        "findAllByAutorId(Long autorId, String query, Pageable pageable) - start");
    Specification<AutorGrupo> specs = AutorGrupoSpecifications.byAutorId(
        autorId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    final Page<AutorGrupo> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByAutorId(Long autorId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Elimina todos los {@link AutorGrupo} cuyo autorId
   * coincide con el indicado.
   * 
   * @param autorId el identificador de la {@link Autor}
   * @return el número de registros eliminados
   */
  public int deleteInBulkByAutorId(Long autorId) {
    log.debug("deleteInBulkByAutorId(Long autorId)  - start");
    final int returnValue = repository.deleteInBulkByAutorId(autorId);
    log.debug("deleteInBulkByAutorId(Long autorId)  - end");
    return returnValue;
  }

  /**
   * Obtiene todos los {@link AutorGrupo} de una {@link ProduccionCientifica}.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @return listado de {@link AutorGrupo}.
   */
  public List<AutorGrupo> findAllByProduccionCientificaId(Long produccionCientificaId) {
    log.debug("findAllByProduccionCientificaId(Long produccionCientificaId)  - start");
    Specification<AutorGrupo> specs = AutorGrupoSpecifications.byProduccionCientificaId(produccionCientificaId);
    final List<AutorGrupo> returnValue = repository.findAll(specs);
    log.debug("findAllByProduccionCientificaId(Long produccionCientificaId)  - end");
    return returnValue;
  }

  /**
   * Obtiene todos los {@link AutorGrupo} de una {@link ProduccionCientifica} y
   * que pertenezcan a uno de los grupos indicados.
   * 
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @param gruposRef              lista de los ids de los grupos.
   * @return listado de {@link AutorGrupo}.
   */
  public List<AutorGrupo> findAllByProduccionCientificaIdAndInGruposRef(Long produccionCientificaId,
      List<Long> gruposRef) {
    log.debug(
        "findAllByProduccionCientificaIdAndInGruposRef(Long produccionCientificaId, List<Long> gruposRef) - start");
    Specification<AutorGrupo> specs = AutorGrupoSpecifications.byProduccionCientificaIdAndInGruposRef(
        produccionCientificaId,
        gruposRef);
    List<AutorGrupo> returnValue = repository.findAll(specs);
    log.debug(
        "findAllByProduccionCientificaIdAndInGruposRef(Long produccionCientificaId, List<Long> gruposRef) - end");
    return returnValue;
  }
}
