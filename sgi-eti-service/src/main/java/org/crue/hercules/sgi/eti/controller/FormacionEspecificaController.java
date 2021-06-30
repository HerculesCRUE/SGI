package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.crue.hercules.sgi.eti.service.FormacionEspecificaService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
   * Crea nueva {@link FormacionEspecifica}.
   * 
   * @param nuevaFormacionEspecifica {@link FormacionEspecifica}. que se quiere
   *                                 crear.
   * @return Nueva {@link FormacionEspecifica} creada.
   */
  @PostMapping
  public ResponseEntity<FormacionEspecifica> newFormacionEspecifica(
      @Valid @RequestBody FormacionEspecifica nuevaFormacionEspecifica) {
    log.debug("newFormacionEspecifica(FormacionEspecifica nuevoFormacionEspecifica) - start");
    FormacionEspecifica returnValue = service.create(nuevaFormacionEspecifica);
    log.debug("newFormacionEspecifica(FormacionEspecifica nuevoFormacionEspecifica) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link FormacionEspecifica}.
   * 
   * @param updatedFormacionEspecifica {@link FormacionEspecifica} a actualizar.
   * @param id                         id {@link FormacionEspecifica} a
   *                                   actualizar.
   * @return {@link FormacionEspecifica} actualizada.
   */
  @PutMapping("/{id}")
  FormacionEspecifica replaceFormacionEspecifica(@Valid @RequestBody FormacionEspecifica updatedFormacionEspecifica,
      @PathVariable Long id) {
    log.debug("replaceFormacionEspecifica(FormacionEspecifica updatedFormacionEspecifica, Long id) - start");
    updatedFormacionEspecifica.setId(id);
    FormacionEspecifica returnValue = service.update(updatedFormacionEspecifica);
    log.debug("replaceFormacionEspecifica(FormacionEspecifica updatedFormacionEspecifica, Long id) - end");
    return returnValue;
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

  /**
   * Elimina {@link FormacionEspecifica} con id indicado.
   * 
   * @param id Identificador de {@link FormacionEspecifica}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    FormacionEspecifica formacionEspecifica = this.one(id);
    formacionEspecifica.setActivo(Boolean.FALSE);
    service.update(formacionEspecifica);
    log.debug("delete(Long id) - end");
  }

}
