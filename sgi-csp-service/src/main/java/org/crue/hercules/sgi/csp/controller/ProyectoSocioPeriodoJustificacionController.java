package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacion;
import org.crue.hercules.sgi.csp.model.ProyectoSocioPeriodoJustificacionDocumento;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionDocumentoService;
import org.crue.hercules.sgi.csp.service.ProyectoSocioPeriodoJustificacionService;
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
 * ProyectoSocioPeriodoJustificacionController
 */

@RestController
@RequestMapping("/proyectosocioperiodojustificaciones")
@Slf4j
public class ProyectoSocioPeriodoJustificacionController {

  /** ProyectoSocioPeriodoJustificacion service */
  private final ProyectoSocioPeriodoJustificacionService service;

  /** ProyectoSocioPeriodoJustificacionDocumentoService service */
  private final ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService;

  /**
   * {@link ProyectoSocioPeriodoJustificacionController}.
   * 
   * @param proyectoSocioPeriodoJustificacionService          {@link ProyectoSocioPeriodoJustificacionService}
   * @param proyectoSocioPeriodoJustificacionDocumentoService {@link ProyectoSocioPeriodoJustificacionDocumentoService}
   */
  public ProyectoSocioPeriodoJustificacionController(
      ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService,
      ProyectoSocioPeriodoJustificacionDocumentoService proyectoSocioPeriodoJustificacionDocumentoService) {
    log.debug(
        "ProyectoSocioPeriodoJustificacionController(ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService) - start");
    this.service = proyectoSocioPeriodoJustificacionService;
    this.proyectoSocioPeriodoJustificacionDocumentoService = proyectoSocioPeriodoJustificacionDocumentoService;
    log.debug(
        "ProyectoSocioPeriodoJustificacionController(ProyectoSocioPeriodoJustificacionService proyectoSocioPeriodoJustificacionService) - end");
  }

  /**
   * Devuelve el {@link ProyectoSocioPeriodoJustificacion} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocioPeriodoJustificacion}.
   * @return {@link ProyectoSocioPeriodoJustificacion} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoSocioPeriodoJustificacion findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoSocioPeriodoJustificacion returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ProyectoSocioPeriodoJustificacion} con el
   * id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocioPeriodoJustificacion}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Void> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Actualiza el listado de {@link ProyectoSocioPeriodoJustificacion} de la
   * {@link ProyectoSocio} con el listado proyectoSocioPeriodoJustificaciones
   * eliminando los elementos segun proceda.
   * 
   * @param proyectoSocioId                     Id de la {@link ProyectoSocio}.
   * @param proyectoSocioPeriodoJustificaciones lista con los nuevos
   *                                            {@link ProyectoSocioPeriodoJustificacion}
   *                                            a guardar.
   * @return Lista actualizada con los {@link ProyectoSocioPeriodoJustificacion}.
   */
  @PatchMapping("/{proyectoSocioId}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<List<ProyectoSocioPeriodoJustificacion>> delete(@PathVariable Long proyectoSocioId,
      @Valid @RequestBody List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) {
    log.debug(
        "delete(Long proyectoSocioId, List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) - start");
    List<ProyectoSocioPeriodoJustificacion> returnValue = service.delete(proyectoSocioId,
        proyectoSocioPeriodoJustificaciones);
    log.debug(
        "delete(Long proyectoSocioId, List<ProyectoSocioPeriodoJustificacion> proyectoSocioPeriodoJustificaciones) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Crea un nuevo {@link ProyectoSocioPeriodoJustificacion}.
   * 
   * @param proyectoSocioPeriodoJustificacion {@link ProyectoSocioPeriodoJustificacion}
   *                                          que se quiere crear.
   * @return Nuevo {@link ProyectoSocioPeriodoJustificacion} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<ProyectoSocioPeriodoJustificacion> create(
      @Valid @RequestBody ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion) {
    log.debug("create(ProyectoSocioPeriodoJustificacion requisitoEquipo) - start");
    ProyectoSocioPeriodoJustificacion returnValue = service.create(proyectoSocioPeriodoJustificacion);
    log.debug("create(ProyectoSocioPeriodoJustificacion requisitoEquipo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoSocioPeriodoJustificacion} con el id de
   * {@link Convocatoria} indicado.
   * 
   * @param proyectoSocioPeriodoJustificacion {@link ProyectoSocioPeriodoJustificacion}
   *                                          a actualizar.
   * @param id                                Identificador de la
   *                                          {@link ProyectoSocioPeriodoJustificacion}.
   * @return {@link ProyectoSocioPeriodoJustificacion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ProyectoSocioPeriodoJustificacion update(
      @Validated({ Update.class,
          Default.class }) @RequestBody ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion,
      @PathVariable Long id) {
    log.debug("update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion, Long id) - start");
    ProyectoSocioPeriodoJustificacion returnValue = service.update(proyectoSocioPeriodoJustificacion, id);
    log.debug("update(ProyectoSocioPeriodoJustificacion proyectoSocioPeriodoJustificacion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada de
   * {@link ProyectoSocioPeriodoJustificacion} de la
   * {@link ProyectoSocioPeriodoJustificacion}.
   * 
   * @param id     Identificador de {@link ProyectoSocioPeriodoJustificacion}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades
   *         {@link ProyectoSocioPeriodoJustificacionDocumento}
   *         paginados y filtrados.
   */
  @GetMapping("/{id}/proyectosocioperiodojustificaciondocumentos")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Page<ProyectoSocioPeriodoJustificacionDocumento>> findAllProyectoSocioPeriodoJustificacion(
      @PathVariable Long id, @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllProyectoSocioPeriodoJustificacion(Long id, String query, Pageable paging) - start");
    Page<ProyectoSocioPeriodoJustificacionDocumento> page = proyectoSocioPeriodoJustificacionDocumentoService
        .findAllByProyectoSocioPeriodoJustificacion(id, query, paging);

    if (page.isEmpty()) {
      log.debug("findAllProyectoSocioPeriodoJustificacion(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllProyectoSocioPeriodoJustificaciones(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Comprueba la existencia documentos relacionados al
   * {@link ProyectoSocioPeriodoJustificacion} con el
   * id indicado.
   * 
   * @param id Identificador de {@link ProyectoSocioPeriodoJustificacion}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/documentos", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Void> existsDocumentos(@PathVariable Long id) {
    log.debug("existsDocumentos(Long id) - start");
    if (service.existsDocumentosById(id)) {
      log.debug("existsDocumentos(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("existsDocumentos(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
