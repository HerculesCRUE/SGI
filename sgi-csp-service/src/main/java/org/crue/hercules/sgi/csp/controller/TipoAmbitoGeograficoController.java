package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.model.TipoAmbitoGeografico;
import org.crue.hercules.sgi.csp.service.TipoAmbitoGeograficoService;
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
 * TipoAmbitoGeograficoController
 */
@RestController
@RequestMapping("/tipoambitogeograficos")
@Slf4j
public class TipoAmbitoGeograficoController {

  /** TipoAmbitoGeografico service */
  private final TipoAmbitoGeograficoService service;

  /**
   * Instancia un nuevo TipoAmbitoGeograficoController.
   * 
   * @param service {@link TipoAmbitoGeograficoService}
   */
  public TipoAmbitoGeograficoController(TipoAmbitoGeograficoService service) {
    this.service = service;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoAmbitoGeografico} activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-V','CSP-CON-INV-V','CSP-CON-C','CSP-CON-E','CSP-CON-B','CSP-CON-R','CSP-PRO-V', 'CSP-PRO-C', 'CSP-PRO-E', 'CSP-PRO-B', 'CSP-PRO-R', 'CSP-FNT-V', 'CSP-FNT-C', 'CSP-FNT-E', 'CSP-FNT-B', 'CSP-FNT-R')")
  ResponseEntity<Page<TipoAmbitoGeografico>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Page<TipoAmbitoGeografico> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findAll(String query, Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoAmbitoGeografico} con el id indicado.
   * 
   * @param id Identificador de {@link TipoAmbitoGeografico}.
   * @return {@link TipoAmbitoGeografico} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-C')")
  TipoAmbitoGeografico findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    TipoAmbitoGeografico returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }
}