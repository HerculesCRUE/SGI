package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.exceptions.EstadoRetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.service.EstadoRetrospectivaService;
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
 * EstadoRetrospectivaController
 */
@RestController
@RequestMapping("/estadoretrospectivas")
@Slf4j
public class EstadoRetrospectivaController {

  /** EstadoRetrospectiva service */
  private EstadoRetrospectivaService service;

  /**
   * Instancia un nuevo EstadoRetrospectivaController.
   * 
   * @param service EstadoRetrospectivaService.
   */
  public EstadoRetrospectivaController(EstadoRetrospectivaService service) {
    log.debug("EstadoRetrospectivaController(EstadoRetrospectivaService service) - start");
    this.service = service;
    log.debug("EstadoRetrospectivaController(EstadoRetrospectivaService service) - end");
  }

  /**
   * Crea {@link EstadoRetrospectiva}.
   *
   * @param estadoRetrospectiva La entidad {@link EstadoRetrospectiva} a crear.
   * @return La entidad {@link EstadoRetrospectiva} creada.
   * @throws IllegalArgumentException Si la entidad {@link EstadoRetrospectiva}
   *                                  tiene id.
   * @return ResponseEntity<EstadoRetrospectiva>
   */
  @PostMapping()
  ResponseEntity<EstadoRetrospectiva> newEstadoRetrospectiva(
      @Valid @RequestBody EstadoRetrospectiva estadoRetrospectiva) {
    log.debug("newEstadoRetrospectiva(EstadoRetrospectiva estadoRetrospectiva) - start");
    EstadoRetrospectiva returnValue = service.create(estadoRetrospectiva);
    log.debug("newEstadoRetrospectiva(EstadoRetrospectiva estadoRetrospectiva) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza la entidad {@link EstadoRetrospectiva}.
   * 
   * @param estadoRetrospectiva La entidad {@link EstadoRetrospectiva} a
   *                            actualizar.
   * @param id                  Identificador de la entidad
   *                            {@link EstadoRetrospectiva}.
   * @return Entidad {@link EstadoRetrospectiva} actualizada.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si la entidad
   *                                              {@link EstadoRetrospectiva} no
   *                                              tiene id.
   */
  @PutMapping("/{id}")
  EstadoRetrospectiva replaceEstadoRetrospectiva(@Valid @RequestBody EstadoRetrospectiva estadoRetrospectiva,
      @PathVariable Long id) {
    log.debug("replaceEstadoRetrospectiva(EstadoRetrospectiva estadoRetrospectiva, Long id) - start");
    estadoRetrospectiva.setId(id);
    EstadoRetrospectiva returnValue = service.update(estadoRetrospectiva);
    log.debug("replaceEstadoRetrospectiva(EstadoRetrospectiva estadoRetrospectiva, Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link EstadoRetrospectiva} por id.
   *
   * @param id El id de la entidad {@link EstadoRetrospectiva}.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si la entidad
   *                                              {@link EstadoRetrospectiva} no
   *                                              tiene id.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    EstadoRetrospectiva estadoRetrospectiva = this.one(id);
    estadoRetrospectiva.setActivo(Boolean.FALSE);
    service.update(estadoRetrospectiva);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las entidades {@link EstadoRetrospectiva} filtradas y paginadas según
   * los criterios de búsqueda.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link EstadoRetrospectiva} paginadas y
   *         filtradas.
   */
  @GetMapping()
  ResponseEntity<Page<EstadoRetrospectiva>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging - start");
    Page<EstadoRetrospectiva> page = service.findAll(query, paging);
    log.debug("findAll(String query, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la entidad {@link EstadoRetrospectiva} por id.
   * 
   * @param id El id de la entidad {@link EstadoRetrospectiva}.
   * @return La entidad {@link EstadoRetrospectiva}.
   * @throws EstadoRetrospectivaNotFoundException Si no existe ninguna entidad
   *                                              {@link EstadoRetrospectiva} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa id.
   */
  @GetMapping("/{id}")
  EstadoRetrospectiva one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    EstadoRetrospectiva returnValue = service.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

}
