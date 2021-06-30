package org.crue.hercules.sgi.eti.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.DictamenNotFoundException;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.repository.DictamenRepository;
import org.crue.hercules.sgi.eti.repository.specification.DictamenSpecifications;
import org.crue.hercules.sgi.eti.service.DictamenService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Dictamen}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class DictamenServiceImpl implements DictamenService {
  private final DictamenRepository dictamenRepository;

  public DictamenServiceImpl(DictamenRepository dictamenRepository) {
    this.dictamenRepository = dictamenRepository;
  }

  /**
   * Guarda la entidad {@link Dictamen}.
   *
   * @param dictamen la entidad {@link Dictamen} a guardar.
   * @return la entidad {@link Dictamen} persistida.
   */
  @Transactional
  public Dictamen create(Dictamen dictamen) {
    log.debug("Petición a create Dictamen : {} - start", dictamen);
    Assert.notNull(dictamen.getId(), "Dictamen id no puede ser null para crear un nuevo dictamen");

    return dictamenRepository.save(dictamen);
  }

  /**
   * Obtiene todas las entidades {@link Dictamen} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Dictamen} paginadas y filtradas.
   */
  public Page<Dictamen> findAll(String query, Pageable paging) {
    log.debug("findAllDictamen(String query, Pageable paging) - start");
    Specification<Dictamen> specs = DictamenSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<Dictamen> returnValue = dictamenRepository.findAll(specs, paging);
    log.debug("findAllDictamen(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Dictamen} por id.
   *
   * @param id el id de la entidad {@link Dictamen}.
   * @return la entidad {@link Dictamen}.
   * @throws DictamenNotFoundException Si no existe ningún {@link Dictamen} con
   *                                   ese id.
   */
  public Dictamen findById(final Long id) throws DictamenNotFoundException {
    log.debug("Petición a get Dictamen : {}  - start", id);
    final Dictamen dictamen = dictamenRepository.findById(id).orElseThrow(() -> new DictamenNotFoundException(id));
    log.debug("Petición a get Dictamen : {}  - end", id);
    return dictamen;

  }

  /**
   * Elimina una entidad {@link Dictamen} por id.
   *
   * @param id el id de la entidad {@link Dictamen}.
   */
  @Transactional
  public void delete(Long id) throws DictamenNotFoundException {
    log.debug("Petición a delete Dictamen : {}  - start", id);
    Assert.notNull(id, "El id de Dictamen no puede ser null.");
    if (!dictamenRepository.existsById(id)) {
      throw new DictamenNotFoundException(id);
    }
    dictamenRepository.deleteById(id);
    log.debug("Petición a delete Dictamen : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link Dictamen}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de Dictamen: {} - start");
    dictamenRepository.deleteAll();
    log.debug("Petición a deleteAll de Dictamen: {} - end");

  }

  /**
   * Actualiza los datos del {@link Dictamen}.
   * 
   * @param dictamenActualizar {@link Dictamen} con los datos actualizados.
   * @return El {@link Dictamen} actualizado.
   * @throws DictamenNotFoundException Si no existe ningún {@link Dictamen} con
   *                                   ese id.
   * @throws IllegalArgumentException  Si el {@link Dictamen} no tiene id.
   */

  @Transactional
  public Dictamen update(final Dictamen dictamenActualizar) {
    log.debug("update(Dictamen dictamenActualizar) - start");

    Assert.notNull(dictamenActualizar.getId(), "Dictamen id no puede ser null para actualizar un dictamen");

    return dictamenRepository.findById(dictamenActualizar.getId()).map(dictamen -> {
      dictamen.setNombre(dictamenActualizar.getNombre());
      dictamen.setTipoEvaluacion(dictamenActualizar.getTipoEvaluacion());
      dictamen.setActivo(dictamenActualizar.getActivo());

      Dictamen returnValue = dictamenRepository.save(dictamen);
      log.debug("update(Dictamen dictamenActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new DictamenNotFoundException(dictamenActualizar.getId()));
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Memoria y el
   * TipoEstadoMemoria sea En secretaría revisión mínima
   * 
   * @return listado de Dictamenes
   */
  @Override
  public List<Dictamen> findAllByMemoriaRevisionMinima() {
    log.debug("findAllByMemoriaRevisionMinima - start");

    // Favorable (1) y Favorable pendiente de revisión mínima (2)
    List<Long> ids = new ArrayList<Long>(Arrays.asList(1L, 2L));
    List<Dictamen> listaDictamenes = dictamenRepository.findByIdIn(ids);

    log.debug("findAllByMemoriaRevisionMinima - end");
    return listaDictamenes;
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Memoria y el
   * TipoEstadoMemoria NO esté En secretaría revisión mínima
   * 
   * @return listado de Dictamenes
   */
  @Override
  public List<Dictamen> findAllByMemoriaNoRevisionMinima() {
    log.debug("findAllByMemoriaNoRevisionMinima - start");

    // Busqueda por TipoEvaluacion: Memoria (devuelve todas las de este tipo)
    List<Dictamen> listaDictamenes = dictamenRepository.findByTipoEvaluacionId(2L);

    log.debug("findAllByMemoriaNoRevisionMinima - end");
    return listaDictamenes;
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Retrospectiva
   * 
   * @return listado de Dictamenes
   */
  @Override
  public List<Dictamen> findAllByRetrospectiva() {
    log.debug("findAllByRetrospectiva - start");

    // Busqueda por TipoEvaluacion: Retrospectiva (devuelve todas las de este tipo)
    List<Dictamen> listaDictamenes = dictamenRepository.findByTipoEvaluacionId(1L);

    log.debug("findAllByRetrospectiva - end");
    return listaDictamenes;
  }

}
