package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.crue.hercules.sgi.csp.service.ProyectoProyectoSgeService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoProyectoSgeController
 */

@RestController
@RequestMapping("/proyecto-proyectos-sge")
@Slf4j
public class ProyectoProyectoSgeController {

  /** ProyectoProyectoSge service */
  private final ProyectoProyectoSgeService service;

  public ProyectoProyectoSgeController(ProyectoProyectoSgeService proyectoProyectoSgeService) {
    this.service = proyectoProyectoSgeService;
  }

  /**
   * Devuelve el {@link ProyectoProyectoSge} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoProyectoSge}.
   * @return {@link ProyectoProyectoSge} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-EJEC-V', 'CSP-EJEC-E')")
  public ProyectoProyectoSge findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoProyectoSge returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea nuevo {@link ProyectoProyectoSge}.
   * 
   * @param proyectoProyectoSge {@link ProyectoProyectoSge} que se quiere crear.
   * @return Nuevo {@link ProyectoProyectoSge} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoProyectoSge> create(@Valid @RequestBody ProyectoProyectoSge proyectoProyectoSge) {
    log.debug("create(ProyectoProyectoSge proyectoProyectoSge) - start");
    ProyectoProyectoSge returnValue = service.create(proyectoProyectoSge);
    log.debug("create(ProyectoProyectoSge proyectoProyectoSge) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Elimina el {@link ProyectoProyectoSge} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoProyectoSge}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link ProyectoProyectoSge} que se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link ProyectoProyectoSge} activas paginadas
   *         y filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-EJEC-V', 'CSP-EJEC-E')")
  public ResponseEntity<Page<ProyectoProyectoSge>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");

    Page<ProyectoProyectoSge> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
