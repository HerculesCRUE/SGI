package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.model.RolSocio;
import org.crue.hercules.sgi.csp.service.RolSocioService;
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
 * RolSocioController
 */
@RestController
@RequestMapping("/rolsocios")
@Slf4j
public class RolSocioController {

  /** RolSocioService service */
  private final RolSocioService service;

  /**
   * Instancia un nuevo RolSocioController.
   * 
   * @param rolSocioService {@link RolSocioService}.
   */
  public RolSocioController(RolSocioService rolSocioService) {
    this.service = rolSocioService;
  }

  /**
   * Devuelve el {@link RolSocio} con el id indicado.
   * 
   * @param id Identificador de {@link RolSocio}.
   * @return RolSocio {@link RolSocio} correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public RolSocio findById(@PathVariable Long id) {
    log.debug("RolSocio findById(Long id) - start");
    RolSocio returnValue = service.findById(id);
    log.debug("RolSocio findById(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link RolSocio} activas.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link RolSocio} activas paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-SOL-E', 'CSP-SOL-V')")
  public ResponseEntity<Page<RolSocio>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<RolSocio> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }
}
