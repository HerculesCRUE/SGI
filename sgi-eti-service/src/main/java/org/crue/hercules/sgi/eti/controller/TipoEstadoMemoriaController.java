package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.service.TipoEstadoMemoriaService;
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
 * TipoEstadoMemoriaController
 */
@RestController
@RequestMapping("/tipoestadomemorias")
@Slf4j
public class TipoEstadoMemoriaController {

  /** TipoEstadoMemoria service */
  private final TipoEstadoMemoriaService service;

  /**
   * Instancia un nuevo TipoEstadoMemoriaController.
   * 
   * @param service TipoEstadoMemoriaService
   */
  public TipoEstadoMemoriaController(TipoEstadoMemoriaService service) {
    log.debug("TipoEstadoMemoriaController(TipoEstadoMemoriaService service) - start");
    this.service = service;
    log.debug("TipoEstadoMemoriaController(TipoEstadoMemoriaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoEstadoMemoria}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<TipoEstadoMemoria>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoEstadoMemoria> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoEstadoMemoria}.
   * 
   * @param nuevoTipoEstadoMemoria {@link TipoEstadoMemoria}. que se quiere crear.
   * @return Nuevo {@link TipoEstadoMemoria} creado.
   */
  @PostMapping
  ResponseEntity<TipoEstadoMemoria> newTipoEstadoMemoria(@Valid @RequestBody TipoEstadoMemoria nuevoTipoEstadoMemoria) {
    log.debug("newTipoEstadoMemoria(TipoEstadoMemoria nuevoTipoEstadoMemoria) - start");
    TipoEstadoMemoria returnValue = service.create(nuevoTipoEstadoMemoria);
    log.debug("newTipoEstadoMemoria(TipoEstadoMemoria nuevoTipoEstadoMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoEstadoMemoria}.
   * 
   * @param updatedTipoEstadoMemoria {@link TipoEstadoMemoria} a actualizar.
   * @param id                       id {@link TipoEstadoMemoria} a actualizar.
   * @return {@link TipoEstadoMemoria} actualizado.
   */
  @PutMapping("/{id}")
  TipoEstadoMemoria replaceTipoEstadoMemoria(@Valid @RequestBody TipoEstadoMemoria updatedTipoEstadoMemoria,
      @PathVariable Long id) {
    log.debug("replaceTipoEstadoMemoria(TipoEstadoMemoria updatedTipoEstadoMemoria, Long id) - start");
    updatedTipoEstadoMemoria.setId(id);
    TipoEstadoMemoria returnValue = service.update(updatedTipoEstadoMemoria);
    log.debug("replaceTipoEstadoMemoria(TipoEstadoMemoria updatedTipoEstadoMemoria, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoEstadoMemoria} con el id indicado.
   * 
   * @param id Identificador de {@link TipoEstadoMemoria}.
   * @return {@link TipoEstadoMemoria} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoEstadoMemoria one(@PathVariable Long id) {
    log.debug("TipoEstadoMemoria one(Long id) - start");
    TipoEstadoMemoria returnValue = service.findById(id);
    log.debug("TipoEstadoMemoria one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoEstadoMemoria} con id indicado.
   * 
   * @param id Identificador de {@link TipoEstadoMemoria}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoEstadoMemoria tipoEstadoMemoria = this.one(id);
    tipoEstadoMemoria.setActivo(Boolean.FALSE);
    service.update(tipoEstadoMemoria);
    log.debug("delete(Long id) - end");
  }

}
