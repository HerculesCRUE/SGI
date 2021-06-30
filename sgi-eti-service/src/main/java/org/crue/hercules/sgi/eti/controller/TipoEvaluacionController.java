package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.service.TipoEvaluacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoEvaluacionController
 */
@RestController
@RequestMapping("/tipoevaluaciones")
@Slf4j
public class TipoEvaluacionController {

  /** TipoEvaluacion service */
  private final TipoEvaluacionService service;

  /**
   * Instancia un nuevo TipoEvaluacionController.
   * 
   * @param service TipoEvaluacionService
   */
  public TipoEvaluacionController(TipoEvaluacionService service) {
    log.debug("TipoEvaluacionController(TipoEvaluacionService service) - start");
    this.service = service;
    log.debug("TipoEvaluacionController(TipoEvaluacionService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoEvaluacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<TipoEvaluacion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoEvaluacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoEvaluacion}.
   * 
   * @param nuevoTipoEvaluacion {@link TipoEvaluacion}. que se quiere crear.
   * @return Nuevo {@link TipoEvaluacion} creado.
   */
  @PostMapping
  public ResponseEntity<TipoEvaluacion> newTipoEvaluacion(@Valid @RequestBody TipoEvaluacion nuevoTipoEvaluacion) {
    log.debug("newTipoEvaluacion(TipoEvaluacion nuevoTipoEvaluacion) - start");
    TipoEvaluacion returnValue = service.create(nuevoTipoEvaluacion);
    log.debug("newTipoEvaluacion(TipoEvaluacion nuevoTipoEvaluacion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoEvaluacion}.
   * 
   * @param updatedTipoEvaluacion {@link TipoEvaluacion} a actualizar.
   * @param id                    id {@link TipoEvaluacion} a actualizar.
   * @return {@link TipoEvaluacion} actualizado.
   */
  @PutMapping("/{id}")
  TipoEvaluacion replaceTipoEvaluacion(@Valid @RequestBody TipoEvaluacion updatedTipoEvaluacion,
      @PathVariable Long id) {
    log.debug("replaceTipoEvaluacion(TipoEvaluacion updatedTipoEvaluacion, Long id) - start");
    updatedTipoEvaluacion.setId(id);
    TipoEvaluacion returnValue = service.update(updatedTipoEvaluacion);
    log.debug("replaceTipoEvaluacion(TipoEvaluacion updatedTipoEvaluacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoEvaluacion} con el id indicado.
   * 
   * @param id Identificador de {@link TipoEvaluacion}.
   * @return {@link TipoEvaluacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoEvaluacion one(@PathVariable Long id) {
    log.debug("TipoEvaluacion one(Long id) - start");
    TipoEvaluacion returnValue = service.findById(id);
    log.debug("TipoEvaluacion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoEvaluacion} con id indicado.
   * 
   * @param id Identificador de {@link TipoEvaluacion}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoEvaluacion tipoEvaluacion = this.one(id);
    tipoEvaluacion.setActivo(Boolean.FALSE);
    service.update(tipoEvaluacion);
    log.debug("delete(Long id) - end");
  }

  /**
   * Devuelve el listado de dictamenes dependiendo del tipo de Evaluación y el
   * flag de Revision Minima de la Evaluación.
   * 
   * @param idTipoEvaluacion Identificador de {@link TipoEvaluacion}.
   * @param esRevisionMinima Flag de Revision Mínima de {@link Evaluacion}.
   * @return Lista de dictamenes
   */
  @GetMapping("/{idTipoEvaluacion}/dictamenes-revision-minima/{esRevisionMinima}")
  List<Dictamen> findAllDictamenByTipoEvaluacionAndRevisionMinima(@PathVariable Long idTipoEvaluacion,
      @PathVariable Boolean esRevisionMinima) {
    log.debug("findAllDictamenByTipoEvaluacionAndRevisionMinima - start");

    List<Dictamen> returnValues = service.findAllDictamenByTipoEvaluacionAndRevisionMinima(idTipoEvaluacion,
        esRevisionMinima);
    log.debug("findAllDictamenByTipoEvaluacionAndRevisionMinima - end");
    return returnValues;
  }

  /**
   * Devuelve el los tipos de evaluación: Memoria y Retrospectiva
   * 
   * @return Lista de tipos de evaluación.
   */
  @GetMapping("/memoria-retrospectiva")
  List<TipoEvaluacion> findTipoEvaluacionMemoriaRetrospectiva() {
    log.debug("findTipoEvaluacionMemoriaRetrospectiva - start");

    List<TipoEvaluacion> returnValues = service.findTipoEvaluacionMemoriaRetrospectiva();
    log.debug("findTipoEvaluacionMemoriaRetrospectiva - end");
    return returnValues;
  }

  /**
   * Devuelve el los tipos de evaluación: Seguimiento Anual y Seguimiento Final
   * 
   * @return Lista de tipos de evaluación.
   */
  @GetMapping("/seguimiento-anual-final")
  List<TipoEvaluacion> findTipoEvaluacionSeguimientoAnualFinal() {
    log.debug("findTipoEvaluacionSeguimientoAnualFinal - start");

    List<TipoEvaluacion> returnValues = service.findTipoEvaluacionSeguimientoAnualFinal();
    log.debug("findTipoEvaluacionSeguimientoAnualFinal - end");
    return returnValues;
  }
}
