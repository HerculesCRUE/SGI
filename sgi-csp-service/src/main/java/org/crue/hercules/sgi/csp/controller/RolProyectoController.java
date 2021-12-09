package org.crue.hercules.sgi.csp.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RolProyectoController
 */
@RestController
@RequestMapping("/rolproyectos")
@Slf4j
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
   * Devuelve el {@link RolProyecto} con el id indicado.
   * 
   * @param id Identificador de {@link RolProyecto}.
   * @return RolProyecto {@link RolProyecto} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
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
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E', 'CSP-SOL-INV-ER')")
  public RolProyecto findPrincipal() {
    log.debug("RolProyecto findById() - start");
    RolProyecto returnValue = service.findPrincipal();
    log.debug("RolProyecto findById() - end");
    return returnValue;
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

}
