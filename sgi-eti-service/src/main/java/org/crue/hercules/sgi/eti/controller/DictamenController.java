package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.service.DictamenService;
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
 * DictamenController
 */
@RestController
@RequestMapping("/dictamenes")
@Slf4j
public class DictamenController {

  /** Dictamen service */
  private final DictamenService service;

  /**
   * Instancia un nuevo DictamenController.
   * 
   * @param service DictamenService
   */
  public DictamenController(DictamenService service) {
    log.debug("DictamenController(DictamenService service) - start");
    this.service = service;
    log.debug("DictamenController(DictamenService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Dictamen}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Dictamen>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<Dictamen> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Memoria y el
   * TipoEstadoMemoria sea En secretaría revisión mínima
   */
  @GetMapping("/memoria-revision-minima")
  List<Dictamen> findAllByMemoriaRevisionMinima() {
    log.debug("findAllByMemoriaRevisionMinima - start");

    List<Dictamen> returnValues = service.findAllByMemoriaRevisionMinima();

    log.debug("findAllByMemoriaRevisionMinima - end");
    return returnValues;
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Memoria y el
   * TipoEstadoMemoria NO esté En secretaría revisión mínima
   */
  @GetMapping("/memoria-no-revision-minima")
  List<Dictamen> findAllByMemoriaNoRevisionMinima() {
    log.debug("findAllByMemoriaNoRevisionMinima - start");

    List<Dictamen> returnValues = service.findAllByMemoriaNoRevisionMinima();

    log.debug("findAllByMemoriaNoRevisionMinima - end");
    return returnValues;
  }

  /**
   * Devuelve los dictamenes para los que el TipoEvaluación sea Retrospectiva
   */
  @GetMapping("/retrospectiva")
  List<Dictamen> findAllByRetrospectiva() {
    log.debug("findAllByRetrospectiva - start");

    List<Dictamen> returnValues = service.findAllByRetrospectiva();
    log.debug("findAllByRetrospectiva - end");
    return returnValues;
  }

  /**
   * Crea nuevo {@link Dictamen}.
   * 
   * @param nuevoDictamen {@link Dictamen}. que se quiere crear.
   * @return Nuevo {@link Dictamen} creado.
   */
  @PostMapping
  ResponseEntity<Dictamen> newDictamen(@Valid @RequestBody Dictamen nuevoDictamen) {
    log.debug("newDictamen(Dictamen nuevoDictamen) - start");
    Dictamen returnValue = service.create(nuevoDictamen);
    log.debug("newDictamen(Dictamen nuevoDictamen) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Dictamen}.
   * 
   * @param updatedDictamen {@link Dictamen} a actualizar.
   * @param id              id {@link Dictamen} a actualizar.
   * @return {@link Dictamen} actualizado.
   */
  @PutMapping("/{id}")
  Dictamen replaceDictamen(@Valid @RequestBody Dictamen updatedDictamen, @PathVariable Long id) {
    log.debug("replaceDictamen(Dictamen updatedDictamen, Long id) - start");
    updatedDictamen.setId(id);
    Dictamen returnValue = service.update(updatedDictamen);
    log.debug("replaceDictamen(Dictamen updatedDictamen, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Dictamen} con el id indicado.
   * 
   * @param id Identificador de {@link Dictamen}.
   * @return {@link Dictamen} correspondiente al id.
   */
  @GetMapping("/{id}")
  Dictamen one(@PathVariable Long id) {
    log.debug("Dictamen one(Long id) - start");
    Dictamen returnValue = service.findById(id);
    log.debug("Dictamen one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Dictamen} con id indicado.
   * 
   * @param id Identificador de {@link Dictamen}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    Dictamen dictamen = this.one(id);
    dictamen.setActivo(Boolean.FALSE);
    service.update(dictamen);
    log.debug("delete(Long id) - end");
  }

}
