package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoEntidad;
import org.crue.hercules.sgi.csp.model.SolicitudProyectoPresupuesto;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoEntidadService;
import org.crue.hercules.sgi.csp.service.SolicitudProyectoPresupuestoService;
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
 * SolicitudProyectoEntidadController
 */
@RestController
@RequestMapping(SolicitudProyectoEntidadController.MAPPING)
@Slf4j
public class SolicitudProyectoEntidadController {
  public static final String MAPPING = "/solicitudesproyectosentidades";

  /** SolicitudProyectoEntidad service */
  private final SolicitudProyectoEntidadService service;
  /** SolicitudProyectoPresupuesto service */
  private final SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService;

  /**
   * Instancia un nuevo SolicitudProyectoEntidadController.
   * 
   * @param service                             {@link SolicitudProyectoEntidadService}
   * @param solicitudProyectoPresupuestoService {@link SolicitudProyectoPresupuestoService}.
   */
  public SolicitudProyectoEntidadController(SolicitudProyectoEntidadService service,
      SolicitudProyectoPresupuestoService solicitudProyectoPresupuestoService) {
    this.service = service;
    this.solicitudProyectoPresupuestoService = solicitudProyectoPresupuestoService;
  }

  /**
   * Devuelve el {@link SolicitudProyectoEntidad} con el id indicado.
   * 
   * @param id Identificador de {@link SolicitudProyectoEntidad}.
   * @return {@link SolicitudProyectoEntidad} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-SOL-E')")
  public SolicitudProyectoEntidad findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    SolicitudProyectoEntidad returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada de {@link SolicitudProyectoPresupuesto}
   * 
   * @param id     Identificador de {@link Solicitud}.
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   * @return el listado de entidades {@link SolicitudProyectoPresupuesto}
   *         paginadas y filtradas.
   */
  @GetMapping("/{id}/solicitudproyectopresupuestos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-SOL-E')")
  public ResponseEntity<Page<SolicitudProyectoPresupuesto>> findAllBySolicitudProyectoEntidad(@PathVariable Long id,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllSolicitudProyectoPresupuestoEntidad(Long id, String query, Pageable paging) - start");
    Page<SolicitudProyectoPresupuesto> page = solicitudProyectoPresupuestoService.findAllBySolicitudProyectoEntidad(id,
        query, paging);

    if (page.isEmpty()) {
      log.debug("findAllSolicitudProyectoPresupuestoEntidad(Long id, String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllSolicitudProyectoPresupuestoEntidad(Long id, String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}