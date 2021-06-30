package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.TipoHito;
import org.crue.hercules.sgi.csp.service.TipoHitoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tipohitos")
@Slf4j
public class TipoHitoController {

  private final TipoHitoService tipoHitoService;

  public TipoHitoController(TipoHitoService tipoHitoService) {
    log.debug("TipoHitoCOntroller(TipoHitoService tipoHitoService) - start");
    this.tipoHitoService = tipoHitoService;
    log.debug("TipoHitoCOntroller(TipoHitoService tipoHitoService) - start");
  }

  /**
   * Devuelve todas las entidades {@link TipoHito} activos paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoHito} paginadas
   */
  @GetMapping()
  @PreAuthorize("hasAuthority('AUTH')")
  ResponseEntity<Page<TipoHito>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoHito> page = tipoHitoService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link TipoHito} paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoHito} paginadas
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-THITO-V', 'CSP-THITO-C', 'CSP-THITO-E', 'CSP-THITO-B', 'CSP-THITO-R', 'CSP-ME-C', 'CSP-ME-E')")
  ResponseEntity<Page<TipoHito>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<TipoHito> page = tipoHitoService.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoHito}.
   * 
   * @param nuevoTipoHito {@link TipoHito}. que se quiere crear.
   * @return returnTipoHito {@link TipoHito} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-THITO-C')")
  public ResponseEntity<TipoHito> create(@Valid @RequestBody TipoHito nuevoTipoHito) {
    log.debug("createTipoHito(TipoHito nuevoTipoHito) - start");
    TipoHito returnTipoHito = tipoHitoService.create(nuevoTipoHito);
    log.debug("createTipoHito(TipoHito nuevoTipoHito) - end");
    return new ResponseEntity<>(returnTipoHito, HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link TipoHito} con el id indicado.
   * 
   * @param id Identificador de {@link TipoHito}.
   * @return returnTipoHito {@link TipoHito} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('AUTH')")
  TipoHito findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoHito returnTipoHito = tipoHitoService.findById(id);
    log.debug("findById(Long id) - end");
    return returnTipoHito;
  }

  /**
   * Actualiza {@link TipoHito}.
   * 
   * @param updatedTipoHito {@link TipoHito} a actualizar.
   * @param id              id {@link TipoHito} a actualizar.
   * @return {@link TipoHito} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-THITO-E')")
  TipoHito update(@PathVariable Long id, @Valid @RequestBody TipoHito updatedTipoHito) {
    log.debug("updateTipoHito(Long id, TipoHito updatedTipoHito) - start");
    updatedTipoHito.setId(id);
    TipoHito returnTipoHito = tipoHitoService.update(updatedTipoHito);
    log.debug("updateTipoHito(Long id, TipoHito updatedTipoHito), Long id) - end");
    return returnTipoHito;
  }

  /**
   * Reactiva el {@link TipoHito} con id indicado.
   * 
   * @param id Identificador de {@link TipoHito}.
   * @return {@link TipoHito} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthority('CSP-THITO-R')")
  TipoHito reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    TipoHito returnValue = tipoHitoService.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link TipoHito} con id indicado.
   * 
   * @param id Identificador de {@link TipoHito}.
   * @return {@link TipoHito} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-THITO-B')")
  TipoHito desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoHito returnValue = tipoHitoService.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }
}