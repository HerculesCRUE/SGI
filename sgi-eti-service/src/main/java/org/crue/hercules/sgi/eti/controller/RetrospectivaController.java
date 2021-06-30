package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.exceptions.RetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RetrospectivaController
 */
@RestController
@RequestMapping("/retrospectivas")
@Slf4j
public class RetrospectivaController {

  /** Retrospectiva service */
  private RetrospectivaService service;

  /**
   * Instancia un nuevo RetrospectivaController.
   * 
   * @param service RetrospectivaService.
   */
  public RetrospectivaController(RetrospectivaService service) {
    log.debug("RetrospectivaController(RetrospectivaService service) - start");
    this.service = service;
    log.debug("RetrospectivaController(RetrospectivaService service) - end");
  }

  /**
   * Crea {@link Retrospectiva}.
   *
   * @param retrospectiva La entidad {@link Retrospectiva} a crear.
   * @return La entidad {@link Retrospectiva} creada.
   * @throws IllegalArgumentException Si la entidad {@link Retrospectiva} tiene
   *                                  id.
   * @return ResponseEntity<Retrospectiva>
   */
  @PostMapping()
  ResponseEntity<Retrospectiva> newRetrospectiva(@Valid @RequestBody Retrospectiva retrospectiva) {
    log.debug("newRetrospectiva(Retrospectiva retrospectiva) - start");
    Retrospectiva returnValue = service.create(retrospectiva);
    log.debug("newRetrospectiva(Retrospectiva retrospectiva) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link Retrospectiva}.
   * 
   * @param retrospectiva La entidad {@link Retrospectiva} a actualizar.
   * @param id            Identificador de la entidad {@link Retrospectiva}.
   * @return Entidad {@link Retrospectiva} actualizada.
   * @throws RetrospectivaNotFoundException Si no existe ninguna entidad
   *                                        {@link Retrospectiva} con ese id.
   * @throws IllegalArgumentException       Si la entidad {@link Retrospectiva} no
   *                                        tiene id.
   */
  @PutMapping("/{id}")
  Retrospectiva replaceRetrospectiva(@Valid @RequestBody Retrospectiva retrospectiva, @PathVariable Long id) {
    log.debug("replaceRetrospectiva(Retrospectiva retrospectiva, Long id) - start");
    retrospectiva.setId(id);
    Retrospectiva returnValue = service.update(retrospectiva);
    log.debug("replaceRetrospectiva(Retrospectiva retrospectiva, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Retrospectiva} por id.
   *
   * @param id El id de la entidad {@link Retrospectiva}.
   * @throws RetrospectivaNotFoundException Si no existe ninguna entidad
   *                                        {@link Retrospectiva} con ese id.
   * @throws IllegalArgumentException       Si la entidad {@link Retrospectiva} no
   *                                        tiene id.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link Retrospectiva} filtradas y paginadas según los
   * criterios de búsqueda.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Retrospectiva} paginadas y filtradas.
   */
  @GetMapping()
  ResponseEntity<Page<Retrospectiva>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging - start");
    Page<Retrospectiva> page = service.findAll(query, paging);
    log.debug("findAll(String query, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la entidad {@link Retrospectiva} por id.
   * 
   * @param id El id de la entidad {@link Retrospectiva}.
   * @return La entidad {@link Retrospectiva}.
   * @throws RetrospectivaNotFoundException Si no existe ninguna entidad
   *                                        {@link Retrospectiva} con ese id.
   * @throws IllegalArgumentException       Si no se informa id.
   */
  @GetMapping("/{id}")
  Retrospectiva one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    Retrospectiva returnValue = service.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

}
