package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.service.ComiteService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
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
 * ComiteController
 */
@RestController
@RequestMapping("/comites")
@Slf4j
public class ComiteController {

  /** Comité service */
  private ComiteService service;

  /** Memoria service */
  private MemoriaService memoriaService;

  /**
   * Instancia un nuevo ComiteController.
   * 
   * @param service        {@link ComiteService}.
   * @param memoriaService {@link MemoriaService}
   */
  public ComiteController(ComiteService service,
      MemoriaService memoriaService) {
    log.debug("ComiteController(ComiteService service) - start");
    this.service = service;
    this.memoriaService = memoriaService;
    log.debug("ComiteController(ComiteService service) - end");
  }

  /**
   * Devuelve el {@link Comite} con el id indicado.
   * 
   * @param id Identificador de {@link Comite}.
   * @return {@link Comite} correspondiente al id.
   */
  @GetMapping("/{id}")
  Comite one(@PathVariable Long id) {
    log.debug("one(Long id) - start");
    Comite returnValue = service.findById(id);
    log.debug("one(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Comite}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-V', 'ETI-CONV-V', 'ETI-EVC-VR', 'ETI-EVC-INV-VR', 'ETI-EVC-EVALR', 'ETI-EVC-INV-EVALR', 'ETI-MEM-INV-VR','ETI-MEM-V')")
  ResponseEntity<Page<Comite>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAllComite(String query,Pageable paging) - start");
    Page<Comite> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAllComite(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAllComite(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Recupera la lista paginada de memorias de un comité.
   * 
   * @param idComite             Identificador de {@link Comite}.
   * @param idPeticionEvaluacion Identificador de la {@link PeticionEvaluacion}.
   * @param pageable             Datos de la paginación.
   * 
   * @return lista paginada de memorias.
   */
  @GetMapping("/{idComite}/memorias-peticion-evaluacion/{idPeticionEvaluacion}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-INV-C', 'ETI-PEV-INV-ER')")
  ResponseEntity<Page<Memoria>> findMemorias(@PathVariable Long idComite, @PathVariable Long idPeticionEvaluacion,
      @RequestPageable(sort = "s") Pageable pageable) {
    log.debug("findMemorias(Long id, Pageable paging) - start");
    Page<Memoria> memorias = memoriaService.findAllMemoriasPeticionEvaluacionModificables(idComite,
        idPeticionEvaluacion, pageable);

    if (memorias.isEmpty()) {
      log.debug("findMemorias(Long id, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findMemorias(Long id, Pageable paging) - end");
    return new ResponseEntity<>(memorias, HttpStatus.OK);
  }

}