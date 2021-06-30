package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.ConflictoInteres;
import org.crue.hercules.sgi.eti.service.ConflictoInteresService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConflictoInteresController
 */
@RestController
@RequestMapping("/conflictosinteres")
@Slf4j
public class ConflictoInteresController {

  /** ConflictoInteres service */
  private final ConflictoInteresService service;

  /**
   * Instancia un nuevo ConflictoInteresController.
   * 
   * @param service ConflictoInteresService
   */
  public ConflictoInteresController(ConflictoInteresService service) {
    log.debug("ConflictoInteresService(ConflictoInteresService service) - start");
    this.service = service;
    log.debug("ConflictoInteresService(ConflictoInteresService service) - end");
  }

  /**
   * Crea un nuevo {@link ConflictoInteres}.
   * 
   * @param nuevoConflictoInteres {@link ConflictoInteres} que se quiere crear.
   * @return Nuevo {@link ConflictoInteres} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVR-C', 'ETI-EVR-E')")
  ResponseEntity<ConflictoInteres> newConflictoInteres(@Valid @RequestBody ConflictoInteres nuevoConflictoInteres) {
    log.debug("newConflictoInteres(ConflictoInteres nuevoConflictoInteres) - start");
    ConflictoInteres returnValue = service.create(nuevoConflictoInteres);
    log.debug("newConflictoInteres(ConflictoInteres nuevoConflictoInteres) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link ConflictoInteres}.
   * 
   * @param updatedConflictoInteres {@link ConflictoInteres} a actualizar.
   * @param id                      id {@link ConflictoInteres} a actualizar.
   * @return {@link ConflictoInteres} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVR-C', 'ETI-EVR-E')")
  ConflictoInteres replaceConflictoInteres(@Valid @RequestBody ConflictoInteres updatedConflictoInteres,
      @PathVariable Long id) {
    log.debug("replaceConflictoInteres(ConflictoInteres updatedConflictoInteres, Long id) - start");
    updatedConflictoInteres.setId(id);
    ConflictoInteres returnValue = service.update(updatedConflictoInteres);
    log.debug("replaceConflictoInteres(ConflictoInteres updatedConflictoInteres, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link ConflictoInteres} con el id indicado.
   * 
   * @param id Identificador de {@link ConflictoInteres}.
   * @return {@link ConflictoInteres} correspondiente al id.
   */
  @GetMapping("/{id}")
  ConflictoInteres one(@PathVariable Long id) {
    log.debug("ConflictoInteres one(Long id) - start");
    ConflictoInteres returnValue = service.findById(id);
    log.debug("ConflictoInteres one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link ConflictoInteres} con id indicado.
   * 
   * @param id Identificador de {@link ConflictoInteres}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
