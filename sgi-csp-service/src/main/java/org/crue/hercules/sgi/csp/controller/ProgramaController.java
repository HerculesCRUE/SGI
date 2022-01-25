package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.service.ProgramaService;
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
 * ProgramaController
 */
@RestController
@RequestMapping("/programas")
@Slf4j
public class ProgramaController {

  /** Programa service */
  private final ProgramaService service;

  /**
   * Instancia un nuevo ProgramaController.
   * 
   * @param service {@link ProgramaService}
   */
  public ProgramaController(ProgramaService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link Programa} con el id indicado.
   * 
   * @param id Identificador de {@link Programa}.
   * @return {@link Programa} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO ('CSP-PRG-E')")
  public Programa findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    Programa returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link Programa}.
   * 
   * @param programa {@link Programa} que se quiere crear.
   * @return Nuevo {@link Programa} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO ('CSP-PRG-C')")
  public ResponseEntity<Programa> create(@Valid @RequestBody Programa programa) {
    log.debug("create(Programa programa) - start");
    Programa returnValue = service.create(programa);
    log.debug("create(Programa programa) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link Programa} con el id indicado.
   * 
   * @param programa {@link Programa} a actualizar.
   * @param id       id {@link Programa} a actualizar.
   * @return {@link Programa} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO ('CSP-PRG-E')")
  public Programa update(@Validated({ Update.class, Default.class }) @RequestBody Programa programa,
      @PathVariable Long id) {
    log.debug("update(Programa programa, Long id) - start");
    programa.setId(id);
    Programa returnValue = service.update(programa);
    log.debug("update(Programa programa, Long id) - end");
    return returnValue;
  }

  /**
   * Reactiva el {@link Programa} con id indicado.
   * 
   * @param id Identificador de {@link Programa}.
   * @return {@link Programa} actualizado.
   */
  @PatchMapping("/{id}/reactivar")
  @PreAuthorize("hasAuthorityForAnyUO ('CSP-PRG-R')")
  public Programa reactivar(@PathVariable Long id) {
    log.debug("reactivar(Long id) - start");
    Programa returnValue = service.enable(id);
    log.debug("reactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link Programa} con id indicado.
   * 
   * @param id Identificador de {@link Programa}.
   * @return {@link Programa} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAnyAuthorityForAnyUO ('CSP-PRG-B', 'CSP-PRG-E')")
  public Programa desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    Programa returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve todas las entidades {@link Programa} activos paginadas
   *
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO ('AUTH')")
  public ResponseEntity<Page<Programa>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<Programa> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link Programa} activos con padre null (los
   * planes) paginadas
   *
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas
   */
  @GetMapping("/plan")
  @PreAuthorize("hasAnyAuthorityForAnyUO ('CSP-CON-C', 'CSP-CON-E','CSP-CON-V','CSP-CON-INV-V', 'CSP-SOL-V', 'CSP-SOL-C', 'CSP-SOL-E', 'CSP-SOL-B', 'CSP-SOL-R', 'CSP-PRO-C', 'CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R')")
  public ResponseEntity<Page<Programa>> findAllPlan(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllPlan(String query, Pageable paging) - start");
    Page<Programa> page = service.findAllPlan(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllPlan(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllPlan(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link Programa} con padre null (los planes)
   * paginadas
   *
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas
   */
  @GetMapping("/plan/todos")
  @PreAuthorize("hasAnyAuthority ('CSP-PRG-V', 'CSP-PRG-C', 'CSP-PRG-E', 'CSP-PRG-B', 'CSP-PRG-R')")
  public ResponseEntity<Page<Programa>> findAllTodosPlan(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllTodosPlan(String query, Pageable paging) - start");
    Page<Programa> page = service.findAllTodosPlan(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllTodosPlan(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllTodosPlan(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve todas las entidades {@link Programa} hijos directos del
   * {@link Programa} con el id indicado paginadas
   *
   * @param id     id del {@link Programa} padre.
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link Programa} paginadas
   */
  @GetMapping("/{id}/hijos")
  @PreAuthorize("hasAnyAuthorityForAnyUO ('CSP-CON-C', 'CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V', 'CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-E', 'CSP-PRG-E')")
  public ResponseEntity<Page<Programa>> findAllHijosPrograma(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllHijosPrograma(String query, Pageable paging) - start");
    Page<Programa> page = service.findAllHijosPrograma(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllHijosPrograma(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllHijosPrograma(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}