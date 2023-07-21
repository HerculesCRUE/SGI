package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;
import org.crue.hercules.sgi.csp.service.RolProyectoColectivoService;
import org.crue.hercules.sgi.csp.service.RolProyectoService;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RolProyectoController
 */
@RestController
@RequestMapping("/rolproyectos")
@Slf4j
@Validated
public class RolProyectoController {

  /** RolProyectoService service */
  private final RolProyectoService service;

  /** RolProyectoColectivoService serviceRolProyectoColectivo */
  RolProyectoColectivoService serviceRolProyectoColectivo;

  /**
   * Instancia un nuevo RolProyectoController.
   * 
   * @param rolProyectoService          {@link RolProyectoService}.
   * @param serviceRolProyectoColectivo {@link RolProyectoColectivoService}
   */
  public RolProyectoController(RolProyectoService rolProyectoService,
      RolProyectoColectivoService serviceRolProyectoColectivo) {
    this.service = rolProyectoService;
    this.serviceRolProyectoColectivo = serviceRolProyectoColectivo;
  }

  /**
   * Crea un nuevo {@link RolProyecto}.
   * 
   * @param rolProyecto {@link RolProyecto} que se quiere crear.
   * @return Nuevo {@link RolProyecto} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO ('CSP-ROLE-C')")
  public ResponseEntity<RolProyecto> create(@Valid @RequestBody RolProyecto rolProyecto) {
    log.debug("create(RolProyecto rolProyecto) - start");
    RolProyecto returnValue = service.create(rolProyecto);
    log.debug("create(RolProyecto rolProyecto) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RolProyecto} con el id indicado.
   * 
   * @param rolProyecto {@link RolProyecto} a actualizar.
   * @param id          id {@link RolProyecto} a actualizar.
   * @return {@link RolProyecto} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO ('CSP-ROLE-E')")
  public RolProyecto update(@Valid @RequestBody RolProyecto rolProyecto, @PathVariable Long id) {
    log.debug("update(RolProyecto rolProyecto, Long id) - start");
    rolProyecto.setId(id);
    RolProyecto returnValue = service.update(rolProyecto);
    log.debug("update(RolProyecto rolProyecto, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link RolProyecto} con el id indicado.
   * 
   * @param id Identificador de {@link RolProyecto}.
   * @return RolProyecto {@link RolProyecto} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-ROLE-E')")
  public RolProyecto findById(@PathVariable Long id) {
    log.debug("RolProyecto findById(Long id) - start");
    RolProyecto returnValue = service.findById(id);
    log.debug("RolProyecto findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link RolProyecto} Principal.
   * 
   * @return RolProyecto {@link RolProyecto} correspondiente al orden "PRINCIPAL"
   */
  @GetMapping("/principal")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-SOL-INV-ER')")
  public RolProyecto findPrincipal() {
    log.debug("RolProyecto findById() - start");
    RolProyecto returnValue = service.findPrincipal();
    log.debug("RolProyecto findById() - end");
    return returnValue;
  }

  /**
   * Devuelve si existe o no el {@link RolProyecto} Principal.
   * 
   * @return true or false
   */
  @RequestMapping(path = "/principal", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ROLE-E', 'CSP-ROLE-V')")
  public ResponseEntity<Void> existsPrincipal() {
    log.debug("existsPrincipal(Long id) - start");
    boolean returnValue = service.existsPrincipal();

    log.debug("existsPrincipal(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RolProyecto} activas.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RolProyecto} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-V', 'CSP-PRO-B', 'CSP-PRO-R','CSP-SOL-INV-ER')")
  public ResponseEntity<Page<RolProyecto>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<RolProyecto> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada {@link RolProyectoColectivo}.
   * 
   * @param id identificador {@link RolProyecto}.
   * @return el listado de entidades {@link RolProyectoColectivo}.
   */
  @GetMapping("/{id}/colectivos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-C', 'CSP-PRO-V', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R', 'CSP-SOL-INV-ER')")
  public ResponseEntity<List<String>> findAllColectivos(@PathVariable Long id) {
    log.debug("findAllColectivos(Long id) - start");
    List<String> listadoColectivos = serviceRolProyectoColectivo.findAllColectivos(id);

    if (listadoColectivos.isEmpty()) {
      log.debug("findAllColectivos(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllColectivos(Long id) - end");
    return new ResponseEntity<>(listadoColectivos, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada {@link RolProyectoColectivo}.
   * 
   * @param id identificador {@link RolProyecto}.
   * @return el listado de entidades {@link RolProyectoColectivo}.
   */
  @GetMapping("/{id}/rol-proyecto-colectivos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ROLE-C', 'CSP-ROLE-V', 'CSP-ROLE-E', 'CSP-ROLE-B', 'CSP-ROLE-R')")
  public ResponseEntity<List<RolProyectoColectivo>> findAllRolProyectoColectivos(@PathVariable Long id) {
    log.debug("findAllRolProyectoColectivos(Long id) - start");
    List<RolProyectoColectivo> colectivos = serviceRolProyectoColectivo.findAllRolProyectoColectivos(id);

    if (colectivos.isEmpty()) {
      log.debug("findAllRolProyectoColectivos(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllRolProyectoColectivos(Long id) - end");
    return new ResponseEntity<>(colectivos, HttpStatus.OK);
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RolProyecto}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link RolProyecto}
   *         paginadas y filtradas.
   */
  @GetMapping("/todos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ROLE-V', 'CSP-ROLE-E','CSP-ROLE-B', 'CSP-ROLE-R')")
  public ResponseEntity<Page<RolProyecto>> findTodos(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findTodos(String query,Pageable paging) - start");
    Page<RolProyecto> page = service.findAllTodos(query, paging);

    if (page.isEmpty()) {
      log.debug("findTodos(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findTodos(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Reactiva el {@link RolProyecto} con id indicado.
   * 
   * @param id Identificador de {@link RolProyecto}.
   * @return {@link RolProyecto} actualizado.
   */
  @PatchMapping("/{id}/activar")
  @PreAuthorize("hasAuthority('CSP-ROLE-R')")
  public RolProyecto activar(@PathVariable Long id) {
    log.debug("activar(Long id) - start");
    RolProyecto returnValue = service.enable(id);
    log.debug("activar(Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link RolProyecto} con id indicado.
   * 
   * @param id Identificador de {@link RolProyecto}.
   * @return {@link RolProyecto} actualizado.
   */
  @PatchMapping("/{id}/desactivar")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ROLE-E', 'CSP-ROLE-B')")
  public RolProyecto desactivar(@PathVariable Long id) {
    log.debug("desactivar(Long id) - start");
    RolProyecto returnValue = service.disable(id);
    log.debug("desactivar(Long id) - end");
    return returnValue;
  }

}
