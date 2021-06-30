package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.model.ProyectoIVA;
import org.crue.hercules.sgi.csp.service.ProyectoIVAService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoIVAController
 */
@RestController
@RequestMapping("/proyectoiva")
@Slf4j
public class ProyectoIVAController {

  /** ProyectoIVA service */
  private final ProyectoIVAService service;

  /**
   * Instancia un nuevo ProyectoIVAController.
   * 
   * @param service {@link ProyectoIVAService}
   */
  public ProyectoIVAController(ProyectoIVAService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link ProyectoIVA}.
   * 
   * @param proyectoIVA {@link ProyectoIVA} que se quiere crear.
   * @return Nuevo {@link ProyectoIVA} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<ProyectoIVA> create(@Valid @RequestBody ProyectoIVA proyectoIVA) {
    log.debug("create(ProyectoIVA proyectoIVA) - start");
    ProyectoIVA returnValue = service.create(proyectoIVA);
    log.debug("create(ProyectoIVA proyectoIVA) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Devuelve todas las entidades {@link ProyectoIVA}
   *
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link ProyectoIVA} paginadas
   */
  @GetMapping("/{proyectoId}/historico")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<Page<ProyectoIVA>> findAllByProyectoId(@PathVariable Long proyectoId,
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findAllByProyectoI(String query, Pageable paging) - start");

    Page<ProyectoIVA> page = service.findAllByProyectoIdOrderByIdDesc(proyectoId, pageable);

    if (page.isEmpty()) {
      log.debug("findAllByProyectoI(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAllByProyectoI(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
