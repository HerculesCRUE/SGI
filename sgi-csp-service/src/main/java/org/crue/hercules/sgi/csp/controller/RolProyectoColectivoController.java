package org.crue.hercules.sgi.csp.controller;

import java.util.List;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.RolProyecto;
import org.crue.hercules.sgi.csp.model.RolProyectoColectivo;
import org.crue.hercules.sgi.csp.service.RolProyectoColectivoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
   * Crea nuevo {@link RolProyectoColectivo}.
   * 
   * @param rolProyectoColectivo {@link RolProyectoColectivo} que se quiere
   *                             crear.
   * @return Nuevo {@link RolProyectoColectivo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-ROLE-E')")
  public ResponseEntity<RolProyectoColectivo> create(@Valid @RequestBody RolProyectoColectivo rolProyectoColectivo) {
    log.debug("create(RolProyectoColectivo rolProyectoColectivo) - start");
    RolProyectoColectivo returnValue = service.create(rolProyectoColectivo);
    log.debug("create(RolProyectoColectivo rolProyectoColectivo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve el listado de colectivos ref vinculados a {@link RolProyecto}
   * activos
   * 
   * @return el listado de colectivos ref vinculados a {@link RolProyecto}
   *         activos.
   */
  @GetMapping(PATH_COLECTIVOS_ACTIVOS)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-GIN-B', 'CSP-GIN-E', 'CSP-GIN-R', 'CSP-GIN-V', 'CSP-GIN-PRC-V', 'CSP-GIN-INV-VR')")
  public ResponseEntity<List<String>> findColectivosActivos() {
    log.debug("findColectivosActivos() - start");
    List<String> colectivos = service.findColectivosActivos();
    log.debug("findColectivosActivos() - end");
    return colectivos.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(colectivos, HttpStatus.OK);
  }

  /**
   * Elimina el {@link RolProyectoColectivo} con id indicado.
   * 
   * @param id Identificador de {@link RolProyectoColectivo}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-ROLE-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
