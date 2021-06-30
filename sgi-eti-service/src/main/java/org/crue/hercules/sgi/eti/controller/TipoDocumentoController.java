package org.crue.hercules.sgi.eti.controller;

import java.util.List;

import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.crue.hercules.sgi.eti.service.TipoDocumentoService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoDocumentoController
 */
@RestController
@RequestMapping("/tipodocumentos")
@Slf4j
public class TipoDocumentoController {

  /** TipoDocumento service */
  private final TipoDocumentoService service;

  /**
   * Instancia un nuevo TipoDocumentoController.
   * 
   * @param service TipoDocumentoService
   */
  public TipoDocumentoController(TipoDocumentoService service) {
    log.debug("TipoDocumentoController(TipoDocumentoService service) - start");
    this.service = service;
    log.debug("TipoDocumentoController(TipoDocumentoService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<TipoDocumento>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoDocumento> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Devuelve el {@link TipoDocumento} con el id indicado.
   * 
   * @param id Identificador de {@link TipoDocumento}.
   * @return {@link TipoDocumento} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoDocumento one(@PathVariable Long id) {
    log.debug("TipoDocumento one(Long id) - start");
    TipoDocumento returnValue = service.findById(id);
    log.debug("TipoDocumento one(Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoDocumento} inicial de un
   * formulario
   * 
   * @param id Identificador de Formulario
   * @return Listado de {@link TipoDocumento}
   */
  @GetMapping("/formulario/{id}")
  ResponseEntity<List<TipoDocumento>> findByFormularioId(@PathVariable Long id) {
    log.debug("findTipoDocumentacionInicial(String query,Pageable paging) - start");
    List<TipoDocumento> page = service.findByFormularioId(id);

    if (page.isEmpty()) {
      log.debug("findTipoDocumentacionInicial(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findTipoDocumentacionInicial(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

}
