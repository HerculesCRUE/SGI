package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.service.InformeService;
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
 * InformeController
 */
@RestController
@RequestMapping("/informeformularios")
@Slf4j
public class InformeController {

  /** Informe service */
  private final InformeService service;

  /**
   * Instancia un nuevo InformeController.
   * 
   * @param service InformeService
   */
  public InformeController(InformeService service) {
    log.debug("InformeController(InformeService service) - start");
    this.service = service;
    log.debug("InformeController(InformeService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Informe}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Informe>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Informe> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Informe}.
   * 
   * @param nuevoInforme {@link Informe}. que se quiere crear.
   * @return Nuevo {@link Informe} creado.
   */
  @PostMapping
  public ResponseEntity<Informe> newInforme(@Valid @RequestBody Informe nuevoInforme) {
    log.debug("newInforme(Informe nuevoInforme) - start");
    Informe returnValue = service.create(nuevoInforme);
    log.debug("newInforme(Informe nuevoInforme) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Informe}.
   * 
   * @param updatedInforme {@link Informe} a actualizar.
   * @param id             id {@link Informe} a actualizar.
   * @return {@link Informe} actualizado.
   */
  @PutMapping("/{id}")
  Informe replaceInforme(@Valid @RequestBody Informe updatedInforme, @PathVariable Long id) {
    log.debug("replaceInforme(Informe updatedInforme, Long id) - start");
    updatedInforme.setId(id);
    Informe returnValue = service.update(updatedInforme);
    log.debug("replaceInforme(Informe updatedInforme, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Informe} con el id indicado.
   * 
   * @param id Identificador de {@link Informe}.
   * @return {@link Informe} correspondiente al id.
   */
  @GetMapping("/{id}")
  Informe one(@PathVariable Long id) {
    log.debug("Informe one(Long id) - start");
    Informe returnValue = service.findById(id);
    log.debug("Informe one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Informe} con id indicado.
   * 
   * @param id Identificador de {@link Informe}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
