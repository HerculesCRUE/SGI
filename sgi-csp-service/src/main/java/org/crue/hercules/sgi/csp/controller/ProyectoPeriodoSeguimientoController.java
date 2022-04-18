package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimientoDocumento;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoPeriodoSeguimientoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoPeriodoSeguimientoController
 */
@RestController
@RequestMapping("/proyectoperiodoseguimientos")
@Slf4j
public class ProyectoPeriodoSeguimientoController {

  /** ProyectoPeriodoSeguimiento service */
  private final ProyectoPeriodoSeguimientoService service;

  /** ProyectoPeriodoSeguimientoDocumentoService service */
  private final ProyectoPeriodoSeguimientoDocumentoService proyectoPeriodoSeguimientoDocumentoService;

  /**
   * Instancia un nuevo ProyectoPeriodoSeguimientoController.
   * 
   * @param service                                    {@link ProyectoPeriodoSeguimientoService}
   * @param proyectoPeriodoSeguimientoDocumentoService {@link ProyectoPeriodoSeguimientoDocumentoService}
   */
  public ProyectoPeriodoSeguimientoController(ProyectoPeriodoSeguimientoService service,
      ProyectoPeriodoSeguimientoDocumentoService proyectoPeriodoSeguimientoDocumentoService) {
    this.service = service;
    this.proyectoPeriodoSeguimientoDocumentoService = proyectoPeriodoSeguimientoDocumentoService;
  }

  /**
   * Crea un nuevo {@link ProyectoPeriodoSeguimiento}.
   * 
   * @param proyectoPeriodoSeguimiento {@link ProyectoPeriodoSeguimiento} que se
   *                                   quiere crear.
   * @return Nuevo {@link ProyectoPeriodoSeguimiento} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoPeriodoSeguimiento> create(
      @Valid @RequestBody ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento) {
    log.debug("create(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento) - start");
    ProyectoPeriodoSeguimiento returnValue = service.create(proyectoPeriodoSeguimiento);
    log.debug("create(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoPeriodoSeguimiento} con el id indicado.
   * 
   * @param proyectoPeriodoSeguimiento {@link ProyectoPeriodoSeguimiento} a
   *                                   actualizar.
   * @param id                         id {@link ProyectoPeriodoSeguimiento} a
   *                                   actualizar.
   * @return {@link ProyectoPeriodoSeguimiento} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoPeriodoSeguimiento update(
      @Validated({ Update.class, Default.class }) @RequestBody ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento,
      @PathVariable Long id) {
    log.debug("update(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento, Long id) - start");
    proyectoPeriodoSeguimiento.setId(id);
    ProyectoPeriodoSeguimiento returnValue = service.update(proyectoPeriodoSeguimiento);
    log.debug("update(ProyectoPeriodoSeguimiento proyectoPeriodoSeguimiento, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoPeriodoSeguimiento} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoSeguimiento}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Comprueba la existencia del {@link ProyectoPeriodoSeguimiento} con el id
   * indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoSeguimiento}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("ProyectoPeriodoSeguimiento exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("ProyectoPeriodoSeguimiento exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("ProyectoPeriodoSeguimiento exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link ProyectoPeriodoSeguimiento} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoPeriodoSeguimiento}.
   * @return {@link ProyectoPeriodoSeguimiento} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoPeriodoSeguimiento findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoPeriodoSeguimiento returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ProyectoPeriodoSeguimientoDocumento} de la
   * {@link ProyectoPeriodoSeguimiento}.
   * 
   * @param id     Identificador de {@link ProyectoPeriodoSeguimiento}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link ProyectoPeriodoSeguimientoDocumento}
   *         paginadas y filtradas.
   */
  @GetMapping("/{id}/proyectoperiodoseguimientodocumentos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E','CSP-PRO-V')")
  public ResponseEntity<Page<ProyectoPeriodoSeguimientoDocumento>> findAllProyectoPeriodoSeguimientoDocumentos(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoPeriodoSeguimientoDocumentos(Long id, String query, Pageable paging) - start");
    Page<ProyectoPeriodoSeguimientoDocumento> page = proyectoPeriodoSeguimientoDocumentoService
        .findAllByProyectoPeriodoSeguimiento(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoPeriodoSeguimientoDocumentos(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoPeriodoSeguimientoDocumentos(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Indica si {@link ProyectoPeriodoSeguimiento} tiene asignados
   * {@link ProyectoPeriodoSeguimientoDocumento}
   * 
   * @param id Identificador de {@link ProyectoPeriodoSeguimiento}.
   * @return true/false
   */
  @RequestMapping(path = "/{id}/proyectoperiodoseguimientodocumentos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Void> existsDocumentos(@PathVariable Long id) {
    log.debug("existsDocumentos(Long id) - start");
    boolean returnValue = proyectoPeriodoSeguimientoDocumentoService.existsByProyectoPeriodoSeguimiento(id);

    log.debug("existsDocumentos(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
