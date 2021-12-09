package org.crue.hercules.sgi.usr.controller;

import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.usr.model.Unidad;
import org.crue.hercules.sgi.usr.service.UnidadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * UnidadController
 */
@RestController
@RequestMapping("/unidades")
@Slf4j
public class UnidadController {

  /** Unidad service */
  private final UnidadService service;

  /**
   * Instancia un nuevo UnidadController.
   * 
   * @param service {@link UnidadService}
   */
  public UnidadController(UnidadService service) {
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Unidad}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R', 'CSP-ME-C', 'CSP-ME-E')")
  public ResponseEntity<Page<Unidad>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<Unidad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Unidad} restringida por los
   * permisos del usuario logueado.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/restringidos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-C','CSP-CON-E','CSP-CON-INV-V', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R')")
  public ResponseEntity<Page<Unidad>> findAllTodosRestringidos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodosRestringidos(String query, Pageable paging, Authentication atuhentication) - start");

    Page<Unidad> page = service.findAllRestringidos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodosRestringidos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodosRestringidos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link Unidad} con el id indicado.
   * 
   * @param id Identificador de {@link Unidad}.
   * @return {@link Unidad} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-E', 'CSP-CON-INV-V', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-ME-E')")
  public Unidad findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Unidad returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}
