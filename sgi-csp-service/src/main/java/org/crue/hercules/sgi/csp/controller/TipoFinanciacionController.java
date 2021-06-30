package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.TipoFinanciacion;
import org.crue.hercules.sgi.csp.service.TipoFinanciacionService;
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
@RequestMapping("/tipofinanciaciones")
@Slf4j

public class TipoFinanciacionController {

  private final TipoFinanciacionService tipoFinanciacionService;

  public TipoFinanciacionController(TipoFinanciacionService tipoFinanciacionService) {

    this.tipoFinanciacionService = tipoFinanciacionService;
  }

  /**
   * Devuelve todas las entidades {@link TipoFinanciacion} activos paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFinanciacion} paginadas
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V', 'CSP-CON-C', 'CSP-CON-E', 'CSP-CON-INV-V', 'CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-E')")
  ResponseEntity<Page<TipoFinanciacion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoFinanciacion> page = tipoFinanciacionService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link TipoFinanciacion} paginadas
   *
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link TipoFinanciacion} paginadas
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-TFNA-V', 'CSP-TFNA-C', 'CSP-TFNA-E', 'CSP-TFNA-B', 'CSP-TFNA-R')")
  ResponseEntity<Page<TipoFinanciacion>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<TipoFinanciacion> page = tipoFinanciacionService.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoFinanciacion}.
   * 
   * @param nuevoTipoFinanciacion {@link TipoFinanciacion}. que se quiere crear.
   * @return returnTipoFinanciacion {@link TipoFinanciacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-TFNA-C')")
  public ResponseEntity<TipoFinanciacion> create(@Valid @RequestBody TipoFinanciacion nuevoTipoFinanciacion) {
    log.debug("createTipoFinanciacion(TipoFinanciacion nuevoTipoFinanciacion) - start");
    TipoFinanciacion returnTipoFinanciacion = tipoFinanciacionService.create(nuevoTipoFinanciacion);
    log.debug("createTipoFinanciacion(TipoFinanciacion nuevoTipoFinanciacion) - end");
    return new ResponseEntity<>(returnTipoFinanciacion, HttpStatus.CREATED);
  }

  /**
   * Devuelve el {@link TipoFinanciacion} con el id indicado.
   * 
   * @param id Identificador de {@link TipoFinanciacion}.
   * @return returnTipoFinanciacion {@link TipoFinanciacion} correspondiente al
   *         id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  TipoFinanciacion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoFinanciacion returnTipoFinanciacion = tipoFinanciacionService.findById(id);
    log.debug("findById(Long id) - end");
    return returnTipoFinanciacion;
  }

  /**
   * Actualiza {@link TipoFinanciacion}.
   * 
   * @param updatedTipoFinanciacion {@link TipoFinanciacion} a actualizar.
   * @param id                      id {@link TipoFinanciacion} a actualizar.
   * @return {@link TipoFinanciacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TFNA-E')")
  TipoFinanciacion update(@PathVariable Long id, @Valid @RequestBody TipoFinanciacion updatedTipoFinanciacion) {
    log.debug("update(Long id, TipoFinanciacion updatedTipoFinanciacion) - start");
    updatedTipoFinanciacion.setId(id);
    TipoFinanciacion returnTipoFinanciacion = tipoFinanciacionService.update(updatedTipoFinanciacion);
    log.debug("update(Long id, TipoFinanciacion updatedTipoFinanciacion), Long id) - end");
    return returnTipoFinanciacion;
  }

  /**
   * Reactiva el {@link TipoFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link TipoFinanciacion}.
   * @return {@link TipoFinanciacion} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TFNA-R')")
  TipoFinanciacion reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    TipoFinanciacion returnValue = tipoFinanciacionService.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link TipoFinanciacion} con id indicado.
   * 
   * @param id Identificador de {@link TipoFinanciacion}.
   * @return {@link TipoFinanciacion} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-TFNA-B')")
  TipoFinanciacion desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    TipoFinanciacion returnValue = tipoFinanciacionService.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

}
