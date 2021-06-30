package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.model.TipoOrigenFuenteFinanciacion;
import org.crue.hercules.sgi.csp.service.TipoOrigenFuenteFinanciacionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoOrigenFuenteFinanciacionController
 */
@RestController
@RequestMapping("/tipoorigenfuentefinanciaciones")
@Slf4j
public class TipoOrigenFuenteFinanciacionController {

  /** TipoOrigenFuenteFinanciacionService service */
  private final TipoOrigenFuenteFinanciacionService service;

  public TipoOrigenFuenteFinanciacionController(TipoOrigenFuenteFinanciacionService service) {
    log.debug("TipoOrigenFuenteFinanciacionController(TipoOrigenFuenteFinanciacionService service) - start");
    this.service = service;
    log.debug("TipoOrigenFuenteFinanciacionController(TipoOrigenFuenteFinanciacionService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoOrigenFuenteFinanciacion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging {@link Pageable}.
   * @return el listado de entidades {@link TipoOrigenFuenteFinanciacion}
   *         paginadas y filtradas.
   */
  @GetMapping()
  ResponseEntity<Page<TipoOrigenFuenteFinanciacion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoOrigenFuenteFinanciacion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
