package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.service.RolProyectoColectivoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RolProyectoController
 */
@RestController
@RequestMapping(RolProyectoColectivoController.REQUEST_MAPPING)
@Slf4j
public class RolProyectoColectivoController {

  public static final String REQUEST_MAPPING = "/rolproyectocolectivos";
  public static final String PATH_COLECTIVOS_ACTIVOS = "/colectivosactivos";

  private final RolProyectoColectivoService service;

  public RolProyectoColectivoController(RolProyectoColectivoService service) {
    this.service = service;
  }

  /**
   * Devuelve el listado de colectivos ref vinculados a {@link RolProyecto}
   * activos
   * 
   * @return el listado de colectivos ref vinculados a {@link RolProyecto}
   *         activos.
   */
  @GetMapping(PATH_COLECTIVOS_ACTIVOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-B', 'CSP-GIN-E', 'CSP-GIN-R', 'CSP-GIN-V', 'CSP-GIN-PRC-V')")
  public ResponseEntity<List<String>> findColectivosActivos() {
    log.debug("findColectivosActivos() - start");
    List<String> colectivos = service.findColectivosActivos();
    log.debug("findColectivosActivos() - end");
    return colectivos.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(colectivos, HttpStatus.OK);
  }

}
