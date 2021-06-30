package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.exceptions.ApartadoNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.service.ApartadoService;
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
 * ApartadoController
 */
@RestController
@RequestMapping("/apartados")
@Slf4j
public class ApartadoController {

  /** Apartado service */
  private ApartadoService service;

  /**
   * Instancia un nuevo ApartadoController.
   * 
   * @param service ApartadoService.
   */
  public ApartadoController(ApartadoService service) {
    log.debug("ApartadoController(ApartadoService service) - start");
    this.service = service;
    log.debug("ApartadoController(ApartadoService service) - end");
  }

  /**
   * Obtiene las entidades {@link Apartado} filtradas y paginadas según los
   * criterios de búsqueda.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping()
  ResponseEntity<Page<Apartado>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query, Pageable paging - start");
    Page<Apartado> page = service.findAll(query, paging);
    log.debug("findAll(String query, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Obtiene la entidad {@link Apartado} por id.
   * 
   * @param id El id de la entidad {@link Apartado}.
   * @return La entidad {@link Apartado}.
   * @throws ApartadoNotFoundException Si no existe ninguna entidad
   *                                   {@link Apartado} con ese id.
   * @throws IllegalArgumentException  Si no se informa id.
   */
  @GetMapping("/{id}")
  Apartado one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    Apartado returnValue = service.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su padre
   * {@link Apartado}.
   * 
   * @param id     El id de la entidad {@link Apartado}.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping("/{id}/hijos")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<Apartado>> getHijos(@PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getHijos(Long id, Pageable paging - start");
    Page<Apartado> page = service.findByPadreId(id, paging);
    log.debug("getHijos(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }
}
