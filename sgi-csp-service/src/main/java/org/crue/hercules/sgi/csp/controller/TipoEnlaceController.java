package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.TipoEnlace;
import org.crue.hercules.sgi.csp.service.TipoEnlaceService;
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

/**
 * TipoEnlaceController
 */

@RestController
@RequestMapping("/tipoenlaces")
@Slf4j
public class TipoEnlaceController {

  /** TipoEnlace service */
  private final TipoEnlaceService service;

  public TipoEnlaceController(TipoEnlaceService tipoEnlaceService) {
    log.debug("TipoEnlaceController(TipoEnlaceService tipoEnlaceService) - start");
    this.service = tipoEnlaceService;
    log.debug("TipoEnlaceController(TipoEnlaceService tipoEnlaceService) - end");
  }

  /**
   * Crea nuevo {@link TipoEnlace}.
   * 
   * @param tipoEnlace {@link TipoEnlace}. que se quiere crear.
   * @return Nuevo {@link TipoEnlace} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-C')")
  public ResponseEntity<TipoEnlace> create(@Valid @RequestBody TipoEnlace tipoEnlace) {
    log.debug("create(TipoEnlace tipoEnlace) - start");
    TipoEnlace returnValue = service.create(tipoEnlace);
    log.debug("create(TipoEnlace tipoEnlace) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoEnlace}.
   * 
   * @param tipoEnlace {@link TipoEnlace} a actualizar.
   * @param id         Identificador {@link TipoEnlace} a actualizar.
   * @return TipoEnlace {@link TipoEnlace} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-E')")
  public TipoEnlace update(@Valid @RequestBody TipoEnlace tipoEnlace, @PathVariable Long id) {
    log.debug("update(TipoEnlace tipoEnlace, Long id) - start");
    tipoEnlace.setId(id);
    TipoEnlace returnValue = service.update(tipoEnlace);
    log.debug("update(TipoEnlace tipoEnlace, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link TipoEnlace} con id indicado.
   * 
   * @param id Identificador de {@link TipoEnlace}.
   * @return {@link TipoEnlace} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-R')")
  public TipoEnlace reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    TipoEnlace returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link TipoEnlace} con id indicado.
   * 
   * @param id Identificador de {@link TipoEnlace}.
   * @return {@link TipoEnlace} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TENL-B')")
  public TipoEnlace desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoEnlace returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoEnlace} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ME-C', 'CSP-ME-E')")
  public ResponseEntity<Page<TipoEnlace>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoEnlace> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoEnlace}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoEnlace} paginadas y filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-TENL-V', 'CSP-TENL-C', 'CSP-TENL-E', 'CSP-TENL-B', 'CSP-TENL-R')")
  public ResponseEntity<Page<TipoEnlace>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<TipoEnlace> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoEnlace} con el id indicado.
   * 
   * @param id Identificador de {@link TipoEnlace}.
   * @return TipoEnlace {@link TipoEnlace} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public TipoEnlace findById(@PathVariable Long id) {
    log.debug("TipoEnlace findById(Long id) - start");
    TipoEnlace returnValue = service.findById(id);
    log.debug("TipoEnlace findById(Long id) - end");
    return returnValue;
  }
}
