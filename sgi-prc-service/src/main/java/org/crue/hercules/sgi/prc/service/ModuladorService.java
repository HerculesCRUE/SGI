package org.crue.hercules.sgi.prc.service;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.exceptions.ConvocatoriaBaremacionNotFoundException;
import org.crue.hercules.sgi.prc.exceptions.ModuladorNotFoundException;
import org.crue.hercules.sgi.prc.model.BaseEntity;
import org.crue.hercules.sgi.prc.model.ConvocatoriaBaremacion;
import org.crue.hercules.sgi.prc.model.Modulador;
import org.crue.hercules.sgi.prc.model.Modulador.TipoModulador;
import org.crue.hercules.sgi.prc.repository.ConvocatoriaBaremacionRepository;
import org.crue.hercules.sgi.prc.repository.ModuladorRepository;
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
 * Service para gestionar {@link Modulador}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class ModuladorService {

  private final ModuladorRepository repository;
  private final ConvocatoriaBaremacionRepository convocatoriaBaremacionRepository;

  /**
   * Guardar un nuevo {@link Modulador}.
   *
   * @param modulador la entidad {@link Modulador}
   *                  a guardar.
   * @return la entidad {@link Modulador} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Create.class })
  public Modulador create(@Valid Modulador modulador) {

    log.debug("create(Modulador modulador) - start");

    AssertHelper.idIsNull(modulador.getId(), Modulador.class);

    Modulador returnValue = repository.save(modulador);

    log.debug("create(Modulador modulador) - end");
    return returnValue;
  }

  /**
   * Actualizar {@link Modulador}.
   *
   * @param modulador la entidad {@link Modulador}
   *                  a actualizar.
   * @return la entidad {@link Modulador} persistida.
   */
  @Transactional
  @Validated({ BaseEntity.Update.class })
  public Modulador update(@Valid Modulador modulador) {
    log.debug("update(Modulador modulador) - start");

    AssertHelper.idNotNull(modulador.getId(), Modulador.class);

    return repository.findById(modulador.getId())
        .map(moduladorExistente -> {

          // Establecemos los campos actualizables con los recibidos
          moduladorExistente.setValor1(modulador.getValor1());
          moduladorExistente.setValor2(modulador.getValor2());
          moduladorExistente.setValor3(modulador.getValor3());
          moduladorExistente.setValor4(modulador.getValor4());
          moduladorExistente.setValor5(modulador.getValor5());

          // Actualizamos la entidad
          Modulador returnValue = repository.save(moduladorExistente);
          log.debug("update(Modulador modulador) - end");
          return returnValue;
        }).orElseThrow(
            () -> new ModuladorNotFoundException(modulador.getId()));
  }

  /**
   * Obtener todas las entidades {@link Modulador} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Modulador} paginadas y/o
   *         filtradas.
   */
  public Page<Modulador> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Modulador> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Modulador> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link Modulador} por su id.
   *
   * @param id el id de la entidad {@link Modulador}.
   * @return la entidad {@link Modulador}.
   */
  public Modulador findById(Long id) {
    log.debug("findById({}) - start", id);

    final Modulador returnValue = repository.findById(id)
        .orElseThrow(() -> new ModuladorNotFoundException(id));
    log.debug("findById({}) - end", id);
    return returnValue;
  }

  /**
   * Elimina la {@link Modulador}.
   *
   * @param id Id del {@link Modulador}.
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete({}) - start", id);
    AssertHelper.idNotNull(id, Modulador.class);

    repository.deleteById(id);
    log.debug("delete({}) - end", id);
  }

  /**
   * Obtiene todos los {@link Modulador} por su convocatoriaBaremacionId
   *
   * @param convocatoriaBaremacionId el id de {@link ConvocatoriaBaremacion}.
   * @return listado de {@link Modulador}.
   */
  public List<Modulador> findByConvocatoriaBaremacionId(Long convocatoriaBaremacionId) {
    log.debug("findByConvocatoriaBaremacionId({})  - start", convocatoriaBaremacionId);

    if (!convocatoriaBaremacionRepository.existsById(convocatoriaBaremacionId)) {
      throw new ConvocatoriaBaremacionNotFoundException(convocatoriaBaremacionId);
    }

    final List<Modulador> returnValue = repository
        .findByConvocatoriaBaremacionId(convocatoriaBaremacionId);
    log.debug("findByConvocatoriaBaremacionId({})  - end", convocatoriaBaremacionId);

    return returnValue;
  }

  /**
   * Obtiene todos los {@link Modulador} por su convocatoriaBaremacionId y tipo
   *
   * @param convocatoriaBaremacionId el id de {@link ConvocatoriaBaremacion}.
   * @param tipo                     {@link TipoModulador}.
   * @return listado de {@link Modulador}.
   */
  public List<Modulador> findByConvocatoriaBaremacionIdAndTipo(Long convocatoriaBaremacionId, TipoModulador tipo) {
    log.debug("findByConvocatoriaBaremacionIdAndTipo({},{})  - start", convocatoriaBaremacionId, tipo);

    if (!convocatoriaBaremacionRepository.existsById(convocatoriaBaremacionId)) {
      throw new ConvocatoriaBaremacionNotFoundException(convocatoriaBaremacionId);
    }

    final List<Modulador> returnValue = repository
        .findByConvocatoriaBaremacionIdAndTipo(convocatoriaBaremacionId, tipo);
    log.debug("findByConvocatoriaBaremacionIdAndTipo({},{})  - end", convocatoriaBaremacionId, tipo);

    return returnValue;
  }

  /**
   * Deletes the given entities.
   *
   * @param entities must not be {@literal null}. Must not contain {@literal null}
   *                 elements.
   * @throws IllegalArgumentException in case the given {@literal entities} or one
   *                                  of its entities is {@literal null}.
   */
  @Transactional
  public void deleteAll(Iterable<Modulador> entities) {
    log.debug("deleteAll(entities) - start");
    repository.deleteAll(entities);
    log.debug("deleteAll(entities) - end");
  }

  /**
   * Elimina todos los {@link Modulador} cuyo
   * convocatoriaBaremacionId coincide con el indicado.
   * 
   * @param convocatoriaBaremacionId el id de la {@link ConvocatoriaBaremacion}
   * @return el número de registros eliminados
   */
  public int deleteInBulkByConvocatoriaBaremacionId(long convocatoriaBaremacionId) {
    log.debug("deleteInBulkByConvocatoriaBaremacionId({})  - start", convocatoriaBaremacionId);
    final int returnValue = repository.deleteInBulkByConvocatoriaBaremacionId(convocatoriaBaremacionId);
    log.debug("deleteInBulkByConvocatoriaBaremacionId({})  - end", convocatoriaBaremacionId);
    return returnValue;
  }
}
