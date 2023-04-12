package org.crue.hercules.sgi.eti.controller;

import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.service.BloqueService;
import org.crue.hercules.sgi.eti.service.FormularioService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * FormularioController
 */
@RestController
@RequestMapping("/formularios")
@Slf4j
public class FormularioController {

  /** Formulario service */
  private final FormularioService formularioService;

  /** Bloque service */
  private final BloqueService bloqueService;

  /**
   * Instancia un nuevo FormularioController.
   * 
   * @param formularioService FormularioService
   * @param bloqueService     BloqueService
   */
  public FormularioController(FormularioService formularioService, BloqueService bloqueService) {
    log.debug("FormularioController(FormularioService service) - start");
    this.formularioService = formularioService;
    this.bloqueService = bloqueService;
    log.debug("FormularioController(FormularioService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Formulario}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<Formulario>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Formulario> page = formularioService.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link Formulario} con el id indicado.
   * 
   * @param id Identificador de {@link Formulario}.
   * @return {@link Formulario} correspondiente al id.
   */
  @GetMapping("/{id}")
  Formulario one(@PathVariable Long id) {
    log.debug("Formulario one(Long id) - start");
    Formulario returnValue = formularioService.findById(id);
    log.debug("Formulario one(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Formulario} a través del id de la memoria.
   * 
   * @param idMemoria Identificador de {@link Memoria}.
   * @return {@link Formulario} correspondiente al id de la memoria.
   */
  @GetMapping("/{idMemoria}/memoria")
  Formulario findByMemoriaId(@PathVariable Long idMemoria) {
    log.debug("Formulario findByMemoriaId(Long idMemoria) - start");
    Formulario returnValue = formularioService.findByMemoriaId(idMemoria);
    log.debug("Formulario findByMemoriaId(Long idMemoria) - end");
    return returnValue;
  }

  @GetMapping("/{id}/bloques")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-PEV-INV-C', 'ETI-PEV-INV-ER')")
  ResponseEntity<Page<Bloque>> getBloques(@PathVariable Long id, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getBloques(Long id, Pageable paging - start");
    Page<Bloque> page = bloqueService.findByFormularioId(id, paging);
    log.debug("getBloques(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Actualiza el estado de la memoria o de la retrospectiva al estado final
   * correspondiente al tipo de formulario completado.
   * 
   * @param memoriaId      identificador de la {@link Memoria}
   * @param tipoFormulario identifcador del tipo de formulario
   *                       {@link Formulario.Tipo}
   * @return {@link HttpStatus#NO_CONTENT}
   */
  @RequestMapping(path = "/completado", method = RequestMethod.HEAD)
  public ResponseEntity<Void> completado(@RequestParam Long memoriaId, @RequestParam Long tipoFormulario) {
    log.debug("completado(Long memoriaId, Long tipoFormulario) - start");
    formularioService.completado(memoriaId, tipoFormulario);
    log.debug("completado(Long memoriaId, Long tipoFormulario) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
