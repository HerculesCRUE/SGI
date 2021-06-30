package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoTarea;
import org.crue.hercules.sgi.eti.service.TipoTareaService;
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
 * TipoTareaController
 */
@RestController
@RequestMapping("/tipostarea")
@Slf4j
public class TipoTareaController {

  /** TipoTarea service */
  private final TipoTareaService service;

  /**
   * Instancia un nuevo TipoTareaController.
   * 
   * @param service TipoTareaService
   */
  public TipoTareaController(TipoTareaService service) {
    log.debug("TipoTareaController(TipoTareaService service) - start");
    this.service = service;
    log.debug("TipoTareaController(TipoTareaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoTarea}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<TipoTarea>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoTarea> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoTarea}.
   * 
   * @param nuevoTipoTarea {@link TipoTarea} que se quiere crear.
   * @return Nuevo {@link TipoTarea} creado.
   */
  @PostMapping
  ResponseEntity<TipoTarea> newTipoTarea(@Valid @RequestBody TipoTarea nuevoTipoTarea) {
    log.debug("newTipoTarea(TipoTarea nuevoTipoTarea) - start");
    TipoTarea returnValue = service.create(nuevoTipoTarea);
    log.debug("newTipoTarea(TipoTarea nuevoTipoTarea) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoTarea}.
   * 
   * @param updatedTipoTarea {@link TipoTarea} a actualizar.
   * @param id               id {@link TipoTarea} a actualizar.
   * @return {@link TipoTarea} actualizado.
   */
  @PutMapping("/{id}")
  TipoTarea replaceTipoTarea(@Valid @RequestBody TipoTarea updatedTipoTarea, @PathVariable Long id) {
    log.debug("replaceTipoTarea(TipoTarea updatedTipoTarea, Long id) - start");
    updatedTipoTarea.setId(id);
    TipoTarea returnValue = service.update(updatedTipoTarea);
    log.debug("replaceTipoTarea(TipoTarea updatedTipoTarea, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoTarea} con el id indicado.
   * 
   * @param id Identificador de {@link TipoTarea}.
   * @return {@link TipoTarea} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoTarea one(@PathVariable Long id) {
    log.debug("TipoTarea one(Long id) - start");
    TipoTarea returnValue = service.findById(id);
    log.debug("TipoTarea one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoTarea} con id indicado.
   * 
   * @param id Identificador de {@link TipoTarea}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoTarea tipoTarea = this.one(id);
    tipoTarea.setActivo(Boolean.FALSE);
    service.update(tipoTarea);
    log.debug("delete(Long id) - end");
  }

}
