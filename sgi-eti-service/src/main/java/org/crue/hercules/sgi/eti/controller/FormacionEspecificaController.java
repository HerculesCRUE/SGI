package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.service.FormacionEspecificaService;
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
 * FormacionEspecificaController
 */
@RestController
@RequestMapping("/formacionespecificas")
@Slf4j
public class FormacionEspecificaController {

  /** FormacionEspecifica service */
  private final FormacionEspecificaService service;

  /**
   * Instancia un nuevo FormacionEspecificaController.
   * 
   * @param service FormacionEspecificaService
   */
  public FormacionEspecificaController(FormacionEspecificaService service) {
    log.debug("FormacionEspecificaController(FormacionEspecificaService service) - start");
    this.service = service;
    log.debug("FormacionEspecificaController(FormacionEspecificaService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link FormacionEspecifica}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<FormacionEspecifica>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<FormacionEspecifica> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link FormacionEspecifica} con el id indicado.
   * 
   * @param id Identificador de {@link FormacionEspecifica}.
   * @return {@link FormacionEspecifica} correspondiente al id.
   */
  @GetMapping("/{id}")
  FormacionEspecifica one(@PathVariable Long id) {
    log.debug("FormacionEspecifica one(Long id) - start");
    FormacionEspecifica returnValue = service.findById(id);
    log.debug("FormacionEspecifica one(Long id) - end");
    return returnValue;
  }

}
