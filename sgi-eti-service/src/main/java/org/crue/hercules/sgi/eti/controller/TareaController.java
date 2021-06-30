package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.service.TareaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TareaController
 */
@RestController
@RequestMapping("/tareas")
@Slf4j
public class TareaController {

  /** Tarea service */
  private final TareaService service;

  /**
   * Instancia un nuevo TareaController.
   * 
   * @param service TareaService
   */
  public TareaController(TareaService service) {
    log.debug("TareaController(TareaService service) - start");
    this.service = service;
    log.debug("TareaController(TareaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Tarea}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Tarea>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Tarea> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Actualiza {@link Tarea}.
   * 
   * @param updatedTarea {@link Tarea} a actualizar.
   * @param id           id {@link Tarea} a actualizar.
   * @return {@link Tarea} actualizada.
   */
  @PutMapping("/{id}")
  Tarea replaceTarea(@Valid @RequestBody Tarea updatedTarea, @PathVariable Long id) {
    log.debug("replaceTarea(Tarea updatedTarea, Long id) - start");
    updatedTarea.setId(id);
    Tarea returnValue = service.update(updatedTarea);
    log.debug("replaceTarea(Tarea updatedTarea, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve la {@link Tarea} con el id indicado.
   * 
   * @param id Identificador de {@link Tarea}.
   * @return {@link Tarea} correspondiente al id.
   */
  @GetMapping("/{id}")
  Tarea one(@PathVariable Long id) {
    log.debug("Tarea one(Long id) - start");
    Tarea returnValue = service.findById(id);
    log.debug("Tarea one(Long id) - end");
    return returnValue;
  }

}
