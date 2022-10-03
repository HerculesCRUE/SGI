package org.crue.hercules.sgi.prc.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.exceptions.IndiceImpactoNotFoundException;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.IndiceImpacto;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.IndiceImpactoRepository;
import org.crue.hercules.sgi.prc.repository.specification.IndiceImpactoSpecifications;
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
 * Service para gestionar {@link IndiceImpacto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class IndiceImpactoService {

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_NAME = "name";

  private final IndiceImpactoRepository repository;
  private final ProduccionCientificaAuthorityHelper authorityHelper;

  public IndiceImpactoService(
      IndiceImpactoRepository indiceImpactoRepository,
      ProduccionCientificaAuthorityHelper authorityHelper) {
    this.repository = indiceImpactoRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guardar un nuevo {@link IndiceImpacto}.
   *
   * @param indiceImpacto la entidad {@link IndiceImpacto}
   *                      a guardar.
   * @return la entidad {@link IndiceImpacto} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public IndiceImpacto create(@Valid IndiceImpacto indiceImpacto) {

    log.debug("create(IndiceImpacto indiceImpacto) - start");

    Assert.isNull(indiceImpacto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(IndiceImpacto.class))
            .build());

    IndiceImpacto returnValue = repository.save(indiceImpacto);

    log.debug("create(IndiceImpacto indiceImpacto) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link IndiceImpacto}.
   *
   * @param indiceImpacto la entidad {@link IndiceImpacto}
   *                      a actualizar.
   * @return la entidad {@link IndiceImpacto} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public IndiceImpacto update(@Valid IndiceImpacto indiceImpacto) {
    log.debug("update(IndiceImpacto indiceImpacto) - start");

    Assert.notNull(indiceImpacto.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(IndiceImpacto.class))
            .build());

    return repository.findById(indiceImpacto.getId())
        .map(indiceImpactoExistente -> {

          // Establecemos los indiceImpactoes actualizables con los recibidos
          indiceImpactoExistente.setFuenteImpacto(indiceImpacto.getFuenteImpacto());
          indiceImpactoExistente.setRanking(indiceImpacto.getRanking());
          indiceImpactoExistente.setAnio(indiceImpacto.getAnio());
          indiceImpactoExistente.setOtraFuenteImpacto(indiceImpacto.getOtraFuenteImpacto());
          indiceImpactoExistente.setIndice(indiceImpacto.getIndice());
          indiceImpactoExistente.setPosicionPublicacion(indiceImpacto.getPosicionPublicacion());
          indiceImpactoExistente.setNumeroRevistas(indiceImpacto.getNumeroRevistas());
          indiceImpactoExistente.setRevista25(indiceImpacto.getRevista25());

          // Actualizamos la entidad
          IndiceImpacto returnValue = repository.save(indiceImpactoExistente);
          log.debug("update(IndiceImpacto indiceImpacto) - end");
          return returnValue;
        }).orElseThrow(
            () -> new IndiceImpactoNotFoundException(indiceImpacto.getId()));
  }

  /**
   * Obtener todas las entidades {@link IndiceImpacto} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link IndiceImpacto} paginadas y/o
   *         filtradas.
   */
  public Page<IndiceImpacto> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<IndiceImpacto> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<IndiceImpacto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link IndiceImpacto} por su id.
   *
   * @param id el id de la entidad {@link IndiceImpacto}.
   * @return la entidad {@link IndiceImpacto}.
   */
  public IndiceImpacto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final IndiceImpacto returnValue = repository.findById(id)
        .orElseThrow(() -> new IndiceImpactoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link IndiceImpacto}.
   *
   * @param id Id del {@link IndiceImpacto}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(IndiceImpacto.class))
            .build());
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene todos los {@link IndiceImpacto} por su produccionCientificaId.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @return listado de {@link IndiceImpacto}.
   */
  public List<IndiceImpacto> findAllByProduccionCientificaId(Long produccionCientificaId) {
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId)  - start");
    final List<IndiceImpacto> returnValue = repository.findAllByProduccionCientificaId(produccionCientificaId);
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId)  - end");
    return returnValue;
  }

  /**
   * Obtiene todos los {@link IndiceImpacto} por su produccionCientificaId
   * paginadas y/o filtradas.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @param query                  la información del filtro.
   * @param pageable               la información de la paginación.
   * @return listado de {@link IndiceImpacto} paginadas y/o filtradas.
   */
  public Page<IndiceImpacto> findAllByProduccionCientificaId(Long produccionCientificaId, String query,
      Pageable pageable) {
    log.debug(
        "findAllByProduccionCientificaId(Long produccionCientificaId, String query, Pageable pageable) - start");
    authorityHelper.checkUserHasAuthorityViewProduccionCientifica(produccionCientificaId);
    Specification<IndiceImpacto> specs = IndiceImpactoSpecifications.byProduccionCientificaId(
        produccionCientificaId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    final Page<IndiceImpacto> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProduccionCientificaId(Long produccionCientificaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Elimina todos los {@link IndiceImpacto} cuyo produccionCientificaId
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
