package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoComentario;
import org.crue.hercules.sgi.eti.service.TipoComentarioService;
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
 * TipoComentarioController
 */
@RestController
@RequestMapping("/tipocomentarios")
@Slf4j
public class TipoComentarioController {

  /** TipoComentario service */
  private final TipoComentarioService service;

  /**
   * Instancia un nuevo TipoComentarioController.
   * 
   * @param service TipoComentarioService
   */
  public TipoComentarioController(TipoComentarioService service) {
    log.debug("TipoComentarioController(TipoComentarioService service) - start");
    this.service = service;
    log.debug("TipoComentarioController(TipoComentarioService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoComentario}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<TipoComentario>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoComentario> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoComentario}.
   * 
   * @param nuevoTipoComentario {@link TipoComentario}. que se quiere crear.
   * @return Nuevo {@link TipoComentario} creado.
   */
  @PostMapping
  ResponseEntity<TipoComentario> newTipoComentario(@Valid @RequestBody TipoComentario nuevoTipoComentario) {
    log.debug("newTipoComentario(TipoComentario nuevoTipoComentario) - start");
    TipoComentario returnValue = service.create(nuevoTipoComentario);
    log.debug("newTipoComentario(TipoComentario nuevoTipoComentario) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoComentario}.
   * 
   * @param updatedTipoComentario {@link TipoComentario} a actualizar.
   * @param id                    id {@link TipoComentario} a actualizar.
   * @return {@link TipoComentario} actualizado.
   */
  @PutMapping("/{id}")
  TipoComentario replaceTipoComentario(@Valid @RequestBody TipoComentario updatedTipoComentario,
      @PathVariable Long id) {
    log.debug("replaceTipoComentario(TipoComentario updatedTipoComentario, Long id) - start");
    updatedTipoComentario.setId(id);
    TipoComentario returnValue = service.update(updatedTipoComentario);
    log.debug("replaceTipoComentario(TipoComentario updatedTipoComentario, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoComentario} con el id indicado.
   * 
   * @param id Identificador de {@link TipoComentario}.
   * @return {@link TipoComentario} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoComentario one(@PathVariable Long id) {
    log.debug("TipoComentario one(Long id) - start");
    TipoComentario returnValue = service.findById(id);
    log.debug("TipoComentario one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoComentario} con id indicado.
   * 
   * @param id Identificador de {@link TipoComentario}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoComentario tipoComentario = this.one(id);
    tipoComentario.setActivo(Boolean.FALSE);
    service.update(tipoComentario);
    log.debug("delete(Long id) - end");
  }

}
