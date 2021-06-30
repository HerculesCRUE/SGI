package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoEstadoActa;
import org.crue.hercules.sgi.eti.service.TipoEstadoActaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * TipoEstadoActaController
 */
@RestController
@RequestMapping("/tipoestadoactas")
@Slf4j
public class TipoEstadoActaController {

  /** TipoEstadoActa service */
  private final TipoEstadoActaService service;

  /**
   * Instancia un nuevo TipoEstadoActaController.
   * 
   * @param service TipoEstadoActaService
   */
  public TipoEstadoActaController(TipoEstadoActaService service) {
    log.debug("TipoEstadoActaController(TipoEstadoActaService service) - start");
    this.service = service;
    log.debug("TipoEstadoActaController(TipoEstadoActaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoEstadoActa}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ACT-V')")
  ResponseEntity<Page<TipoEstadoActa>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoEstadoActa> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoEstadoActa}.
   * 
   * @param nuevoTipoEstadoActa {@link TipoEstadoActa}. que se quiere crear.
   * @return Nuevo {@link TipoEstadoActa} creado.
   */
  @PostMapping
  public ResponseEntity<TipoEstadoActa> newTipoEstadoActa(@Valid @RequestBody TipoEstadoActa nuevoTipoEstadoActa) {
    log.debug("newTipoEstadoActa(TipoEstadoActa nuevoTipoEstadoActa) - start");
    TipoEstadoActa returnValue = service.create(nuevoTipoEstadoActa);
    log.debug("newTipoEstadoActa(TipoEstadoActa nuevoTipoEstadoActa) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoEstadoActa}.
   * 
   * @param updatedTipoEstadoActa {@link TipoEstadoActa} a actualizar.
   * @param id                    id {@link TipoEstadoActa} a actualizar.
   * @return {@link TipoEstadoActa} actualizado.
   */
  @PutMapping("/{id}")
  TipoEstadoActa replaceTipoEstadoActa(@Valid @RequestBody TipoEstadoActa updatedTipoEstadoActa,
      @PathVariable Long id) {
    log.debug("replaceTipoEstadoActa(TipoEstadoActa updatedTipoEstadoActa, Long id) - start");
    updatedTipoEstadoActa.setId(id);
    TipoEstadoActa returnValue = service.update(updatedTipoEstadoActa);
    log.debug("replaceTipoEstadoActa(TipoEstadoActa updatedTipoEstadoActa, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoEstadoActa} con el id indicado.
   * 
   * @param id Identificador de {@link TipoEstadoActa}.
   * @return {@link TipoEstadoActa} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoEstadoActa one(@PathVariable Long id) {
    log.debug("TipoEstadoActa one(Long id) - start");
    TipoEstadoActa returnValue = service.findById(id);
    log.debug("TipoEstadoActa one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoEstadoActa} con id indicado.
   * 
   * @param id Identificador de {@link TipoEstadoActa}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoEstadoActa tipoEstadoActa = this.one(id);
    tipoEstadoActa.setActivo(Boolean.FALSE);
    service.update(tipoEstadoActa);
    log.debug("delete(Long id) - end");
  }

}
