package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.model.EquipoTrabajo;
import org.crue.hercules.sgi.eti.service.EquipoTrabajoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * EquipoTrabajoController
 */
@RestController
@RequestMapping("/equipotrabajos")
@Slf4j
public class EquipoTrabajoController {

  /** EquipoTrabajo service */
  private final EquipoTrabajoService service;

  /**
   * Instancia un nuevo EquipoTrabajoController.
   * 
   * @param service EquipoTrabajoService
   */
  public EquipoTrabajoController(EquipoTrabajoService service) {
    log.debug("EquipoTrabajoController(EquipoTrabajoService service) - start");
    this.service = service;
    log.debug("EquipoTrabajoController(EquipoTrabajoService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link EquipoTrabajo}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<EquipoTrabajo>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<EquipoTrabajo> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link EquipoTrabajo} con el id indicado.
   * 
   * @param id Identificador de {@link EquipoTrabajo}.
   * @return {@link EquipoTrabajo} correspondiente al id.
   */
  @GetMapping("/{id}")
  EquipoTrabajo one(@PathVariable Long id) {
    log.debug("EquipoTrabajo one(Long id) - start");
    EquipoTrabajo returnValue = service.findById(id);
    log.debug("EquipoTrabajo one(Long id) - end");
    return returnValue;
  }

}
