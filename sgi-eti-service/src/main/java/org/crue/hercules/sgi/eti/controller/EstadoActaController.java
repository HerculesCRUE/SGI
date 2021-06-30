package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.EstadoActa;
import org.crue.hercules.sgi.eti.service.EstadoActaService;
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
 * EstatoActaController
 */
@RestController
@RequestMapping("/estadoactas")
@Slf4j
public class EstadoActaController {

  /** EstadoActa service */
  private final EstadoActaService service;

  /**
   * Instancia un nuevo EstadoActaController.
   * 
   * @param service EstadoActaService
   */
  public EstadoActaController(EstadoActaService service) {
    log.debug("EstadoActaController(EstadoActaService service) - start");
    this.service = service;
    log.debug("EstadoActaController(EstadoActaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link EstadoActa}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<EstadoActa>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<EstadoActa> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link EstadoActa}.
   * 
   * @param nuevoEstadoActa {@link EstadoActa} que se quiere crear.
   * @return Nuevo {@link EstadoActa} creado.
   */
  @PostMapping
  public ResponseEntity<EstadoActa> newEstadoActa(@Valid @RequestBody EstadoActa nuevoEstadoActa) {
    log.debug("newEstadoActa(EstadoActa nuevoEstadoActa) - start");
    EstadoActa returnValue = service.create(nuevoEstadoActa);
    log.debug("newEstadoActa(EstadoActa nuevoEstadoActa) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link EstadoActa}.
   * 
   * @param updatedEstadoActa {@link EstadoActa} a actualizar.
   * @param id                id {@link EstadoActa} a actualizar.
   * @return {@link EstadoActa} actualizado.
   */
  @PutMapping("/{id}")
  EstadoActa replaceEstadoActa(@Valid @RequestBody EstadoActa updatedEstadoActa, @PathVariable Long id) {
    log.debug("replaceEstadoActa(EstadoActa updatedEstadoActa, Long id) - start");
    updatedEstadoActa.setId(id);
    EstadoActa returnValue = service.update(updatedEstadoActa);
    log.debug("replaceEstadoActa(EstadoActa updatedEstadoActa, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link EstadoActa} con el id indicado.
   * 
   * @param id Identificador de {@link EstadoActa}.
   * @return {@link EstadoActa} correspondiente al id.
   */
  @GetMapping("/{id}")
  EstadoActa one(@PathVariable Long id) {
    log.debug("EstadoActa one(Long id) - start");
    EstadoActa returnValue = service.findById(id);
    log.debug("EstadoActa one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link EstadoActa} con id indicado.
   * 
   * @param id Identificador de {@link EstadoActa}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
