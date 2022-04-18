package org.crue.hercules.sgi.prc.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.exceptions.AcreditacionNotFoundException;
import org.crue.hercules.sgi.prc.model.Acreditacion;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.AcreditacionRepository;
import org.crue.hercules.sgi.prc.repository.specification.AcreditacionSpecifications;
import org.crue.hercules.sgi.prc.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link Acreditacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class AcreditacionService {

  private final AcreditacionRepository repository;

  /**
   * Guardar un nuevo {@link Acreditacion}.
   *
   * @param acreditacion la entidad {@link Acreditacion}
   *                     a guardar.
   * @return la entidad {@link Acreditacion} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Acreditacion create(@Valid Acreditacion acreditacion) {

    log.debug("create(Acreditacion acreditacion) - start");

    AssertHelper.idIsNull(acreditacion.getId(), Acreditacion.class);

    Acreditacion returnValue = repository.save(acreditacion);

    log.debug("create(Acreditacion acreditacion) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Acreditacion}.
   *
   * @param acreditacion la entidad {@link Acreditacion}
   *                     a actualizar.
   * @return la entidad {@link Acreditacion} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public Acreditacion update(@Valid Acreditacion acreditacion) {
    log.debug("update(Acreditacion acreditacion) - start");

    AssertHelper.idNotNull(acreditacion.getId(), Acreditacion.class);

    return repository.findById(acreditacion.getId())
        .map(acreditacionExistente -> {

          // Establecemos los acreditaciones actualizables con los recibidos
          acreditacionExistente.setDocumentoRef(acreditacion.getDocumentoRef());
          acreditacionExistente.setUrl(acreditacion.getUrl());

          // Actualizamos la entidad
          Acreditacion returnValue = repository.save(acreditacionExistente);
          log.debug("update(Acreditacion acreditacion) - end");
          return returnValue;
        }).orElseThrow(
            () -> new AcreditacionNotFoundException(acreditacion.getId()));
  }

  /**
   * Obtener todas las entidades {@link Acreditacion} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Acreditacion} paginadas y/o
   *         filtradas.
   */
  public Page<Acreditacion> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Acreditacion> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Acreditacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene todos los {@link Acreditacion} por su produccionCientificaId
   * paginadas y/o filtradas.
   *
   * @param produccionCientificaId el id de {@link ProduccionCientifica}.
   * @param query                  la información del filtro.
   * @param pageable               la información de la paginación.
   * @return listado de {@link Acreditacion} paginadas y/o filtradas.
   */
  public Page<Acreditacion> findAllByProduccionCientificaId(Long produccionCientificaId, String query,
      Pageable pageable) {
    log.debug(
        "findAllByProduccionCientificaId(Long prodduccionCientificaId, String query, Pageable pageable) - start");
    Specification<Acreditacion> specs = AcreditacionSpecifications.byProduccionCientificaId(
        produccionCientificaId)
        .and(SgiRSQLJPASupport.toSpecification(query));
    final Page<Acreditacion> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByProduccionCientificaId(Long prodduccionCientificaId, String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Acreditacion} por su id.
   *
   * @param id el id de la entidad {@link Acreditacion}.
   * @return la entidad {@link Acreditacion}.
   */
  public Acreditacion findById(Long id) {
    log.debug("findById(Long id)  - start");
    final Acreditacion returnValue = repository.findById(id)
        .orElseThrow(() -> new AcreditacionNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Elimina la {@link Acreditacion}.
   *
   * @param id Id del {@link Acreditacion}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    AssertHelper.idNotNull(id, Acreditacion.class);

    repository.deleteById(id);
    log.debug("delete(Long id) - end");

  }

  /**
   * Elimina todos los {@link Acreditacion} cuyo produccionCientificaId
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
