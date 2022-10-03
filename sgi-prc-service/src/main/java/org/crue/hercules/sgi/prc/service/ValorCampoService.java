package org.crue.hercules.sgi.prc.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.CampoProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.exceptions.NoRelatedEntitiesException;
import org.crue.hercules.sgi.prc.exceptions.ValorCampoNotFoundException;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.CampoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.model.ValorCampo;
import org.crue.hercules.sgi.prc.repository.CampoProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.ValorCampoRepository;
import org.crue.hercules.sgi.prc.repository.specification.ValorCampoSpecifications;
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
 * Service para gestionar {@link ValorCampo}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ValorCampoService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final ValorCampoRepository repository;
  private final CampoProduccionCientificaRepository campoProduccionCientificaRepository;
  private final ProduccionCientificaAuthorityHelper authorityHelper;

  public ValorCampoService(
      ValorCampoRepository valorCampoRepository,
      CampoProduccionCientificaRepository campoProduccionCientificaRepository,
      ProduccionCientificaAuthorityHelper authorityHelper) {
    this.repository = valorCampoRepository;
    this.campoProduccionCientificaRepository = campoProduccionCientificaRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guardar un nuevo {@link ValorCampo}.
   *
   * @param valorCampo la entidad {@link ValorCampo}
   *                   a guardar.
   * @return la entidad {@link ValorCampo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public ValorCampo create(@Valid ValorCampo valorCampo) {

    log.debug("create(ValorCampo valorCampo) - start");

    Assert.isNull(valorCampo.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ValorCampo.class))
            .build());

    ValorCampo returnValue = repository.save(valorCampo);

    log.debug("create(ValorCampo valorCampo) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link ValorCampo}.
   *
   * @param valorCampo la entidad {@link ValorCampo}
   *                   a actualizar.
   * @return la entidad {@link ValorCampo} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public ValorCampo update(@Valid ValorCampo valorCampo) {
    log.debug("update(ValorCampo valorCampo) - start");

    Assert.notNull(valorCampo.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ValorCampo.class))
            .build());

    return repository.findById(valorCampo.getId())
        .map(valorCampoExistente -> {

          // Establecemos los valorCampos actualizables con los recibidos
          valorCampoExistente.setOrden(valorCampo.getOrden());
          valorCampoExistente.setValor(valorCampo.getValor());

          // Actualizamos la entidad
          ValorCampo returnValue = repository.save(valorCampoExistente);
          log.debug("update(ValorCampo valorCampo) - end");
          return returnValue;
        }).orElseThrow(
            () -> new ValorCampoNotFoundException(valorCampo.getId()));
  }

  /**
   * Obtener todas las entidades {@link ValorCampo} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ValorCampo} paginadas y/o
   *         filtradas.
   */
  public Page<ValorCampo> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ValorCampo> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ValorCampo> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ValorCampo} por su id.
   *
   * @param id el id de la entidad {@link ValorCampo}.
   * @return la entidad {@link ValorCampo}.
   */
  public ValorCampo findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ValorCampo returnValue = repository.findById(id)
        .orElseThrow(() -> new ValorCampoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link ValorCampo}.
   *
   * @param id Id del {@link ValorCampo}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(ValorCampo.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene todos los {@link ValorCampo} por su campoProduccionCientificaId
   * paginadas y/o filtradas.
   *
   * @param produccionCientificaId      el id de {@link ProduccionCientifica}.
   * @param campoProduccionCientificaId el id de
   *                                    {@link CampoProduccionCientifica}.
   * @param query                       la información del filtro.
   * @param pageable                    la información de la paginación.
   * @return listado de {@link ValorCampo} paginadas y/o filtradas.
   */
  public Page<ValorCampo> findAllByCampoProduccionCientificaId(Long produccionCientificaId,
      Long campoProduccionCientificaId, String query,
      Pageable pageable) {
    log.debug(
        "findAllByCampoProduccionCientificaId(Long produccionCientificaId, Long campoProduccionCientificaId, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewProduccionCientifica(produccionCientificaId);

    final CampoProduccionCientifica relatedCampoProduccionCientifica = campoProduccionCientificaRepository
        .findById(campoProduccionCientificaId)
        .orElseThrow(() -> new CampoProduccionCientificaNotFoundException(campoProduccionCientificaId.toString()));
    if (!produccionCientificaId.equals(relatedCampoProduccionCientifica.getProduccionCientificaId())) {
      throw new NoRelatedEntitiesException(CampoProduccionCientifica.class, ProduccionCientifica.class);
    }

    Specification<ValorCampo> specs = ValorCampoSpecifications
        .byCampoProduccionCientificaId(campoProduccionCientificaId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    final Page<ValorCampo> returnValue = repository.findAll(specs, pageable);
    log.debug(
        "findAllByCampoProduccionCientificaId(Long produccionCientificaId, Long campoProduccionCientificaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Elimina todos los {@link ValorCampo} cuyo campoProduccionCientificaId
   * coincide con el indicado.
   * 
   * @param campoProduccionCientificaId el identificador de la
   *                                    {@link CampoProduccionCientifica}
   * @return el número de registros eliminados
   */
  public int deleteInBulkByCampoProduccionCientificaId(Long campoProduccionCientificaId) {
    log.debug("deleteInBulkByCampoProduccionCientificaId(Long campoProduccionCientificaId)  - start");
    final int returnValue = repository.deleteInBulkByCampoProduccionCientificaId(campoProduccionCientificaId);
    log.debug("deleteInBulkByCampoProduccionCientificaId(Long campoProduccionCientificaId)  - end");
    return returnValue;
  }
}