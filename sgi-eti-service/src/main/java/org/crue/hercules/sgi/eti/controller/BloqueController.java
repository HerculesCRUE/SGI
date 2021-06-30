package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.service.ApartadoService;
import org.crue.hercules.sgi.eti.service.BloqueService;
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
 * BloqueController
 */
@RestController
@RequestMapping("/bloques")
@Slf4j
public class BloqueController {

  /** Bloque service */
  private final BloqueService service;

  /** Apartado service */
  private ApartadoService apartadoService;

  /**
   * Instancia un nuevo BloqueController.
   * 
   * @param service         BloqueService
   * @param apartadoService ApartadoService
   */
  public BloqueController(BloqueService service, ApartadoService apartadoService) {
    log.debug("BloqueController(BloqueService service, ApartadoService apartadoService) - start");
    this.service = service;
    this.apartadoService = apartadoService;
    log.debug("BloqueController(BloqueService service, ApartadoService apartadoService) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Bloque}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Bloque>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Bloque> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link Bloque} con el id indicado.
   * 
   * @param id Identificador de {@link Bloque}.
   * @return {@link Bloque} correspondiente al id.
   */
  @GetMapping("/{id}")
  Bloque one(@PathVariable Long id) {
    log.debug("Bloque one(Long id) - start");
    Bloque returnValue = service.findById(id);
    log.debug("Bloque one(Long id) - end");
    return returnValue;
  }

  /**
   * Obtiene las entidades {@link Apartado} por el id de su {@link Bloque}.
   * 
   * @param id     El id de la entidad {@link Bloque}.
   * @param paging pageable
   * @return el listado de entidades {@link Apartado} paginadas y filtradas.
   */
  @GetMapping("/{id}/apartados")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<Apartado>> getApartados(@PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getApartados(Long id, Pageable paging - start");
    Page<Apartado> page = apartadoService.findByBloqueId(id, paging);
    log.debug("getApartados(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
