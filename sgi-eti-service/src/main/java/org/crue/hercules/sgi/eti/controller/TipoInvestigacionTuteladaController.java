package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;
import org.crue.hercules.sgi.eti.service.TipoInvestigacionTuteladaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoInvestigacionTuteladaController
 */
@RestController
@RequestMapping("/tipoinvestigaciontuteladas")
@Slf4j
public class TipoInvestigacionTuteladaController {

  /** TipoInvestigacionTutelada service */
  private final TipoInvestigacionTuteladaService service;

  /**
   * Instancia un nuevo TipoInvestigacionTuteladaController.
   * 
   * @param service TipoInvestigacionTuteladaService
   */
  public TipoInvestigacionTuteladaController(TipoInvestigacionTuteladaService service) {
    log.debug("TipoInvestigacionTuteladaController(TipoInvestigacionTuteladaService service) - start");
    this.service = service;
    log.debug("TipoInvestigacionTuteladaController(TipoInvestigacionTuteladaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoInvestigacionTutelada}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV')")
  ResponseEntity<Page<TipoInvestigacionTutelada>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoInvestigacionTutelada> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
