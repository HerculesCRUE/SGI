package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.service.EstadoMemoriaService;
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
 * EstadoMemoriaController
 */
@RestController
@RequestMapping("/estadomemorias")
@Slf4j
public class EstadoMemoriaController {

  /** EstadoMemoria service */
  private final EstadoMemoriaService service;

  /**
   * Instancia un nuevo EstadoMemoriaController.
   * 
   * @param service EstadoMemoriaService
   */
  public EstadoMemoriaController(EstadoMemoriaService service) {
    log.debug("EstadoMemoriaController(EstadoMemoriaService service) - start");
    this.service = service;
    log.debug("EstadoMemoriaController(EstadoMemoriaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link EstadoMemoria}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<EstadoMemoria>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<EstadoMemoria> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link EstadoMemoria}.
   * 
   * @param nuevoEstadoMemoria {@link EstadoMemoria}. que se quiere crear.
   * @return Nuevo {@link EstadoMemoria} creado.
   */
  @PostMapping
  public ResponseEntity<EstadoMemoria> newEstadoMemoria(@Valid @RequestBody EstadoMemoria nuevoEstadoMemoria) {
    log.debug("newEstadoMemoria(EstadoMemoria nuevoEstadoMemoria) - start");
    EstadoMemoria returnValue = service.create(nuevoEstadoMemoria);
    log.debug("newEstadoMemoria(EstadoMemoria nuevoEstadoMemoria) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link EstadoMemoria}.
   * 
   * @param updatedEstadoMemoria {@link EstadoMemoria} a actualizar.
   * @param id                   id {@link EstadoMemoria} a actualizar.
   * @return {@link EstadoMemoria} actualizado.
   */
  @PutMapping("/{id}")
  EstadoMemoria replaceEstadoMemoria(@Valid @RequestBody EstadoMemoria updatedEstadoMemoria, @PathVariable Long id) {
    log.debug("replaceEstadoMemoria(EstadoMemoria updatedEstadoMemoria, Long id) - start");
    updatedEstadoMemoria.setId(id);
    EstadoMemoria returnValue = service.update(updatedEstadoMemoria);
    log.debug("replaceEstadoMemoria(EstadoMemoria updatedEstadoMemoria, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link EstadoMemoria} con el id indicado.
   * 
   * @param id Identificador de {@link EstadoMemoria}.
   * @return {@link EstadoMemoria} correspondiente al id.
   */
  @GetMapping("/{id}")
  EstadoMemoria one(@PathVariable Long id) {
    log.debug("EstadoMemoria one(Long id) - start");
    EstadoMemoria returnValue = service.findById(id);
    log.debug("EstadoMemoria one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link EstadoMemoria} con id indicado.
   * 
   * @param id Identificador de {@link EstadoMemoria}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
