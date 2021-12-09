package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia;
import org.crue.hercules.sgi.csp.service.TipoRegimenConcurrenciaService;
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
 * TipoRegimenConcurrenciaController
 */

@RestController
@RequestMapping("/tiporegimenconcurrencias")
@Slf4j
public class TipoRegimenConcurrenciaController {

  /** TipoRegimenConcurrencia service */
  private final TipoRegimenConcurrenciaService service;

  public TipoRegimenConcurrenciaController(TipoRegimenConcurrenciaService tipoRegimenConcurrenciaService) {
    log.debug(
        "TipoRegimenConcurrenciaController(TipoRegimenConcurrenciaService tipoRegimenConcurrenciaService) - start");
    this.service = tipoRegimenConcurrenciaService;
    log.debug("TipoRegimenConcurrenciaController(TipoRegimenConcurrenciaService tipoRegimenConcurrenciaService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoRegimenConcurrencia}
   * activos.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoRegimenConcurrencia} paginadas y
   *         filtradas.
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-INV-V', 'CSP-CON-V', 'CSP-CON-E')")
  ResponseEntity<Page<TipoRegimenConcurrencia>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoRegimenConcurrencia> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoRegimenConcurrencia} con el id indicado.
   * 
   * @param id Identificador de {@link TipoRegimenConcurrencia}.
   * @return TipoRegimenConcurrencia {@link TipoRegimenConcurrencia}
   *         correspondiente al id
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  TipoRegimenConcurrencia findById(@PathVariable Long id) {
    log.debug("TipoRegimenConcurrencia findById(Long id) - start");
    TipoRegimenConcurrencia returnValue = service.findById(id);
    log.debug("TipoRegimenConcurrencia findById(Long id) - end");
    return returnValue;
  }
}
