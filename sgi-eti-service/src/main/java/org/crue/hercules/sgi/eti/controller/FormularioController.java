package org.crue.hercules.sgi.eti.controller;

import java.io.IOException;

import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.service.BloqueService;
import org.crue.hercules.sgi.eti.service.FormularioService;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.spring.context.i18n.SgiLocaleContextHolder;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

  @GetMapping("/{id}/bloques")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-PEV-INV-C', 'ETI-PEV-INV-ER')")
  ResponseEntity<Page<Bloque>> getBloques(@PathVariable Long id,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("getBloques(Long id, Pageable paging - start");
    bloqueService.findByFormularioId(id, paging);
    Page<Bloque> page = bloqueService.findByFormularioId(id, paging);
    log.debug("getBloques(Long id, Pageable paging - end");
    if (page.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  @GetMapping("/{id}/report")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ResponseEntity<byte[]> getReport(@PathVariable Long id,
      @RequestParam(name = "l", required = false) String lang) {
    log.debug("getResource({}) - start", id);
    Language language = Language.fromCode(lang);
    if (language == null) {
      language = SgiLocaleContextHolder.getLanguage();
    }

    byte[] returnValue = formularioService.getReport(id, language);
    log.debug("getResource({}) - end", id);

    return ResponseEntity.ok().body(returnValue);
  }

  @RequestMapping(path = "/{id}/report", method = RequestMethod.HEAD)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ResponseEntity<Void> existsReport(@PathVariable Long id,
      @RequestParam(name = "l", required = false) String lang) {
    Language language = Language.fromCode(lang);
    if (language == null) {
      language = SgiLocaleContextHolder.getLanguage();
    }
    boolean returnValue = formularioService.existReport(id, language);
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
  public ResponseEntity<Void> completado(@RequestParam Long memoriaId, @RequestParam Formulario.Tipo tipoFormulario) {
    log.debug("completado(Long memoriaId, Long tipoFormulario) - start");
    formularioService.completado(memoriaId, tipoFormulario);
    log.debug("completado(Long memoriaId, Long tipoFormulario) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Update or create {@link Resource} value.
   * 
   * @param id   id {@link Formulario} identificador del formulario
   * @param file {@link Resource} value
   * @return HTTP 200
   */
  @PatchMapping("/{id}/report")
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ResponseEntity<Void> updateValue(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
    log.debug("updateValue({}, MultipartFile file) - start", id);

    try {
      formularioService.updateReport(id, SgiLocaleContextHolder.getLanguage(), file.getBytes());
    } catch (IOException e) {
      log.error("updateValue({}, MultipartFile file)", e);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    log.debug("updateValue({}, MultipartFile file) - end", id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
