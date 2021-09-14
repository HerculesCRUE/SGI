package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConceptoGasto;
import org.crue.hercules.sgi.csp.service.ConceptoGastoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
 * ConceptoGastoController
 */
@RestController
@RequestMapping("/conceptogastos")
@Slf4j
public class ConceptoGastoController {

  /** ConceptoGasto service */
  private final ConceptoGastoService service;

  /**
   * Instancia un nuevo ConceptoGastoController.
   * 
   * @param service {@link ConceptoGastoService}
   */
  public ConceptoGastoController(ConceptoGastoService service) {
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ConceptoGasto} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V', 'CSP-SOL-E', 'CSP-SOL-V')")
  ResponseEntity<Page<ConceptoGasto>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ConceptoGasto> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ConceptoGasto}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthority('CSP-TGTO-V', 'CSP-TGTO-C', 'CSP-TGTO-E', 'CSP-TGTO-B', 'CSP-TGTO-R')")
  ResponseEntity<Page<ConceptoGasto>> findAllTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodos(String query, Pageable paging) - start");
    Page<ConceptoGasto> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodos(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodos(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link ConceptoGasto} con el id indicado.
   * 
   * @param id Identificador de {@link ConceptoGasto}.
   * @return {@link ConceptoGasto} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ConceptoGasto findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConceptoGasto returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ConceptoGasto}.
   * 
   * @param conceptoGasto {@link ConceptoGasto} que se quiere crear.
   * @return Nuevo {@link ConceptoGasto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('CSP-TGTO-C')")
  ResponseEntity<ConceptoGasto> create(@Valid @RequestBody ConceptoGasto conceptoGasto) {
    log.debug("create(ConceptoGasto conceptoGasto) - start");
    ConceptoGasto returnValue = service.create(conceptoGasto);
    log.debug("create(ConceptoGasto conceptoGasto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConceptoGasto} con el id indicado.
   * 
   * @param conceptoGasto {@link ConceptoGasto} a actualizar.
   * @param id            id {@link ConceptoGasto} a actualizar.
   * @return {@link ConceptoGasto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('CSP-TGTO-E')")
  ConceptoGasto update(@Validated({ Update.class, Default.class }) @RequestBody ConceptoGasto conceptoGasto,
      @PathVariable Long id) {
    log.debug("update(ConceptoGasto conceptoGasto, Long id) - start");
    conceptoGasto.setId(id);
    ConceptoGasto returnValue = service.update(conceptoGasto);
    log.debug("update(ConceptoGasto conceptoGasto, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link ConceptoGasto} con id indicado.
   * 
   * @param id Identificador de {@link ConceptoGasto}.
   * @return {@link ConceptoGasto} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthority('CSP-TGTO-R')")
  ConceptoGasto reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    ConceptoGasto returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConceptoGasto} con id indicado.
   * 
   * @param id Identificador de {@link ConceptoGasto}.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAuthority('CSP-TGTO-B')")
  ConceptoGasto desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    ConceptoGasto returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  @GetMapping("/{id}/no-proyectoagrupacion")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ConceptoGasto>> findAllNotInAgrupacion(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging, @PathVariable Long id) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<ConceptoGasto> page = service.findAllNotInAgrupacion(id, query, paging);
    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
