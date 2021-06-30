package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.service.TipoMemoriaService;
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
 * TipoMemoriaController
 */
@RestController
@RequestMapping("/tipomemorias")
@Slf4j
public class TipoMemoriaController {

  /** TipoMemoria service */
  private final TipoMemoriaService service;

  /**
   * Instancia un nuevo TipoMemoriaController.
   * 
   * @param service TipoMemoriaService
   */
  public TipoMemoriaController(TipoMemoriaService service) {
    log.debug("TipoMemoriaController(TipoMemoriaService service) - start");
    this.service = service;
    log.debug("TipoMemoriaController(TipoMemoriaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoMemoria}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<TipoMemoria>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoMemoria> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoMemoria}.
   * 
   * @param nuevoTipoMemoria {@link TipoMemoria}. que se quiere crear.
   * @return Nuevo {@link TipoMemoria} creado.
   */
  @PostMapping
  public ResponseEntity<TipoMemoria> newTipoMemoria(@Valid @RequestBody TipoMemoria nuevoTipoMemoria) {
    log.debug("newTipoMemoria(TipoMemoria nuevoTipoMemoria) - start");
    TipoMemoria returnValue = service.create(nuevoTipoMemoria);
    log.debug("newTipoMemoria(TipoMemoria nuevoTipoMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoMemoria}.
   * 
   * @param updatedTipoMemoria {@link TipoMemoria} a actualizar.
   * @param id                 id {@link TipoMemoria} a actualizar.
   * @return {@link TipoMemoria} actualizado.
   */
  @PutMapping("/{id}")
  TipoMemoria replaceTipoMemoria(@Valid @RequestBody TipoMemoria updatedTipoMemoria, @PathVariable Long id) {
    log.debug("replaceTipoMemoria(TipoMemoria updatedTipoMemoria, Long id) - start");
    updatedTipoMemoria.setId(id);
    TipoMemoria returnValue = service.update(updatedTipoMemoria);
    log.debug("replaceTipoMemoria(TipoMemoria updatedTipoMemoria, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoMemoria} con el id indicado.
   * 
   * @param id Identificador de {@link TipoMemoria}.
   * @return {@link TipoMemoria} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoMemoria one(@PathVariable Long id) {
    log.debug("TipoMemoria one(Long id) - start");
    TipoMemoria returnValue = service.findById(id);
    log.debug("TipoMemoria one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoMemoria} con id indicado.
   * 
   * @param id Identificador de {@link TipoMemoria}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoMemoria tipoMemoria = this.one(id);
    tipoMemoria.setActivo(Boolean.FALSE);
    service.update(tipoMemoria);
    log.debug("delete(Long id) - end");
  }

}
