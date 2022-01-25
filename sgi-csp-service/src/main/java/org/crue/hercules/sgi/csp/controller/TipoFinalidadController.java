package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.TipoFinalidad;
import org.crue.hercules.sgi.csp.service.TipoFinalidadService;
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
 * TipoFinalidadController
 */

@RestController
@RequestMapping("/tipofinalidades")
@Slf4j
public class TipoFinalidadController {

  /** TipoFinalidad service */
  private final TipoFinalidadService service;

  public TipoFinalidadController(TipoFinalidadService tipoFinalidadService) {
    log.debug("TipoFinalidadController(TipoFinalidadService tipoFinalidadService) - start");
    this.service = tipoFinalidadService;
    log.debug("TipoFinalidadController(TipoFinalidadService tipoFinalidadService) - end");
  }

  /**
   * Crea nuevo {@link TipoFinalidad}.
   * 
   * @param tipoFinalidad {@link TipoFinalidad}. que se quiere crear.
   * @return Nuevo {@link TipoFinalidad} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-TFIN-C')")
  public ResponseEntity<TipoFinalidad> create(@Valid @RequestBody TipoFinalidad tipoFinalidad) {
    log.debug("create(TipoFinalidad tipoFinalidad) - start");
    TipoFinalidad returnValue = service.create(tipoFinalidad);
    log.debug("create(TipoFinalidad tipoFinalidad) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoFinalidad}.
   * 
   * @param tipoFinalidad {@link TipoFinalidad} a actualizar.
   * @param id            Identificador {@link TipoFinalidad} a actualizar.
   * @return TipoFinalidad {@link TipoFinalidad} actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-TFIN-E')")
  public TipoFinalidad update(@Valid @RequestBody TipoFinalidad tipoFinalidad, @PathVariable Long id) {
    log.debug("update(TipoFinalidad tipoFinalidad, Long id) - start");
    tipoFinalidad.setId(id);
    TipoFinalidad returnValue = service.update(tipoFinalidad);
    log.debug("update(TipoFinalidad tipoFinalidad, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link TipoFinalidad} con id indicado.
   * 
   * @param id Identificador de {@link TipoFinalidad}.
   * @return {@link TipoFinalidad} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthority('CSP-TFIN-R')")
  public TipoFinalidad reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    TipoFinalidad returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link TipoFinalidad} con id indicado.
   * 
   * @param id Identificador de {@link TipoFinalidad}.
   * @return {@link TipoFinalidad} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-TFIN-B')")
  public TipoFinalidad desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoFinalidad returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoFinalidad} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoFinalidad} paginadas y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-INV-V', 'CSP-ME-C', 'CSP-ME-E', 'CSP-PRO-E', 'CSP-PRO-V', 'CSP-PRO-MOD-V')")
  public ResponseEntity<Page<TipoFinalidad>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoFinalidad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoFinalidad}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoFinalidad} paginadas y filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-TFIN-V','CSP-TFIN-C','CSP-TFIN-E', 'CSP-TFIN-B', 'CSP-TFIN-R')")
  public ResponseEntity<Page<TipoFinalidad>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query,Pageable paging) - start");
    Page<TipoFinalidad> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllTodos(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoFinalidad} con el id indicado.
   * 
   * @param id Identificador de {@link TipoFinalidad}.
   * @return TipoFinalidad {@link TipoFinalidad} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  public TipoFinalidad findById(@PathVariable Long id) {
    log.debug("TipoFinalidad findById(Long id) - start");
    TipoFinalidad returnValue = service.findById(id);
    log.debug("TipoFinalidad findById(Long id) - end");
    return returnValue;
  }
}
