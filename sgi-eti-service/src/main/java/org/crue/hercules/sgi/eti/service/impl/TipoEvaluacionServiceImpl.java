package org.crue.hercules.sgi.eti.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.TipoEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.repository.DictamenRepository;
import org.crue.hercules.sgi.eti.repository.TipoEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.specification.TipoEvaluacionSpecifications;
import org.crue.hercules.sgi.eti.service.TipoEvaluacionService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoEvaluacion}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoEvaluacionServiceImpl implements TipoEvaluacionService {
  private final TipoEvaluacionRepository tipoEvaluacionRepository;
  private final DictamenRepository dictamenRepository;

  public TipoEvaluacionServiceImpl(TipoEvaluacionRepository tipoEvaluacionRepository,
      DictamenRepository dictamenRepository) {
    this.tipoEvaluacionRepository = tipoEvaluacionRepository;
    this.dictamenRepository = dictamenRepository;
  }

  /**
   * Guarda la entidad {@link TipoEvaluacion}.
   *
   * @param tipoEvaluacion la entidad {@link TipoEvaluacion} a guardar.
   * @return la entidad {@link TipoEvaluacion} persistida.
   */
  @Transactional
  public TipoEvaluacion create(TipoEvaluacion tipoEvaluacion) {
    log.debug("Petición a create TipoEvaluacion : {} - start", tipoEvaluacion);
    Assert.notNull(tipoEvaluacion.getId(), "TipoEvaluacion id no puede ser null para crear un nuevo tipoEvaluacion");

    return tipoEvaluacionRepository.save(tipoEvaluacion);
  }

  /**
   * Obtiene todas las entidades {@link TipoEvaluacion} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link TipoEvaluacion} paginadas y filtradas.
   */
  public Page<TipoEvaluacion> findAll(String query, Pageable paging) {
    log.debug("findAllTipoEvaluacion(String query,Pageable paging) - start");
    Specification<TipoEvaluacion> specs = TipoEvaluacionSpecifications.activos()
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<TipoEvaluacion> returnValue = tipoEvaluacionRepository.findAll(specs, paging);
    log.debug("findAllTipoEvaluacion(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link TipoEvaluacion} por id.
   *
   * @param id el id de la entidad {@link TipoEvaluacion}.
   * @return la entidad {@link TipoEvaluacion}.
   * @throws TipoEvaluacionNotFoundException Si no existe ningún
   *                                         {@link TipoEvaluacion}e con ese id.
   */
  public TipoEvaluacion findById(final Long id) throws TipoEvaluacionNotFoundException {
    log.debug("Petición a get TipoEvaluacion : {}  - start", id);
    final TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
        .orElseThrow(() -> new TipoEvaluacionNotFoundException(id));
    log.debug("Petición a get TipoEvaluacion : {}  - end", id);
    return tipoEvaluacion;

  }

  /**
   * Elimina una entidad {@link TipoEvaluacion} por id.
   *
   * @param id el id de la entidad {@link TipoEvaluacion}.
   */
  @Transactional
  public void delete(Long id) throws TipoEvaluacionNotFoundException {
    log.debug("Petición a delete TipoEvaluacion : {}  - start", id);
    Assert.notNull(id, "El id de TipoEvaluacion no puede ser null.");
    if (!tipoEvaluacionRepository.existsById(id)) {
      throw new TipoEvaluacionNotFoundException(id);
    }
    tipoEvaluacionRepository.deleteById(id);
    log.debug("Petición a delete TipoEvaluacion : {}  - end", id);
  }

  /**
   * Elimina todos los registros {@link TipoEvaluacion}.
   */
  @Transactional
  public void deleteAll() {
    log.debug("Petición a deleteAll de TipoEvaluacion: {} - start");
    tipoEvaluacionRepository.deleteAll();
    log.debug("Petición a deleteAll de TipoEvaluacion: {} - end");

  }

  /**
   * Actualiza los datos del {@link TipoEvaluacion}.
   * 
   * @param tipoEvaluacionActualizar {@link TipoEvaluacion} con los datos
   *                                 actualizados.
   * @return El {@link TipoEvaluacion} actualizado.
   * @throws TipoEvaluacionNotFoundException Si no existe ningún
   *                                         {@link TipoEvaluacion} con ese id.
   * @throws IllegalArgumentException        Si el {@link TipoEvaluacion} no tiene
   *                                         id.
   */

  @Transactional
  public TipoEvaluacion update(final TipoEvaluacion tipoEvaluacionActualizar) {
    log.debug("update(TipoEvaluacion TipoEvaluacionActualizar) - start");

    Assert.notNull(tipoEvaluacionActualizar.getId(),
        "TipoEvaluacion id no puede ser null para actualizar un tipo Evaluacion");

    return tipoEvaluacionRepository.findById(tipoEvaluacionActualizar.getId()).map(tipoEvaluacion -> {
      tipoEvaluacion.setNombre(tipoEvaluacionActualizar.getNombre());
      tipoEvaluacion.setActivo(tipoEvaluacionActualizar.getActivo());

      TipoEvaluacion returnValue = tipoEvaluacionRepository.save(tipoEvaluacion);
      log.debug("update(TipoEvaluacion tipoEvaluacionActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new TipoEvaluacionNotFoundException(tipoEvaluacionActualizar.getId()));
  }

  @Override
  public List<Dictamen> findAllDictamenByTipoEvaluacionAndRevisionMinima(Long idTipoEvaluacion,
      Boolean esRevisionMinima) {

    log.debug("findAllDictamenByTipoEvaluacionAndRevisionMinima - start");
    List<Dictamen> listaDictamenes = new ArrayList<Dictamen>();

    // TipoEvaluacion: Memoria y esRevisionMinima: True
    if ((idTipoEvaluacion == 2L) && (esRevisionMinima)) {

      // Favorable (1) y Favorable pendiente de revisión mínima (2)
      List<Long> ids = new ArrayList<Long>(Arrays.asList(1L, 2L));
      listaDictamenes = dictamenRepository.findByIdIn(ids);

      return listaDictamenes;
    }

    // TipoEvaluacion: Memoria y esRevisionMinima: False
    if ((idTipoEvaluacion == 2L) && (!esRevisionMinima)) {

      // Busqueda por TipoEvaluacion: Memoria (devuelve todas las de este tipo)
      listaDictamenes = dictamenRepository.findByTipoEvaluacionId(idTipoEvaluacion);

    }

    // TipoEvaluacion: Retrospectiva
    if (idTipoEvaluacion == 1L) {

      // Busqueda por TipoEvaluacion: Retrospectiva (devuelve todas las de este tipo)
      listaDictamenes = dictamenRepository.findByTipoEvaluacionId(idTipoEvaluacion);

    }

    // TipoEvaluacion: Seguimiento Anual
    if (idTipoEvaluacion == 3L) {

      // Busqueda por TipoEvaluacion: Seguimiento Anual (devuelve todas las de este
      // tipo)
      listaDictamenes = dictamenRepository.findByTipoEvaluacionId(idTipoEvaluacion);

    }

    // TipoEvaluacion: Seguimiento Final
    if (idTipoEvaluacion == 4L) {

      // Busqueda por TipoEvaluacion: Seguimiento Final (devuelve todas las de este
      // tipo)
      listaDictamenes = dictamenRepository.findByTipoEvaluacionId(idTipoEvaluacion);

    }
    log.debug("findAllDictamenByTipoEvaluacionAndRevisionMinima - end");
    return listaDictamenes;
  }

  @Override
  public List<TipoEvaluacion> findTipoEvaluacionMemoriaRetrospectiva() {
    log.debug("findTipoEvaluacionMemoriaRetrospectiva - start");

    List<Long> lista = new ArrayList<Long>(Arrays.asList(1L, 2L));
    List<TipoEvaluacion> listaTipoEvaluacion = tipoEvaluacionRepository.findByActivoTrueAndIdIn(lista);

    log.debug("findTipoEvaluacionMemoriaRetrospectiva - end");
    return listaTipoEvaluacion;
  }

  @Override
  public List<TipoEvaluacion> findTipoEvaluacionSeguimientoAnualFinal() {
    log.debug("findTipoEvaluacionSeguimientoAnualFinal - start");

    List<Long> lista = new ArrayList<Long>(Arrays.asList(3L, 4L));
    List<TipoEvaluacion> listaTipoEvaluacion = tipoEvaluacionRepository.findByActivoTrueAndIdIn(lista);

    log.debug("findTipoEvaluacionSeguimientoAnualFinal - end");
    return listaTipoEvaluacion;
  }

}
