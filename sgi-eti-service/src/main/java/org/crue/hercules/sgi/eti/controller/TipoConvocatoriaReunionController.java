package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoConvocatoriaReunion;
import org.crue.hercules.sgi.eti.service.TipoConvocatoriaReunionService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * TipoConvocatoriaReunionController
 */
@RestController
@RequestMapping("/tipoconvocatoriareuniones")
@Slf4j
public class TipoConvocatoriaReunionController {

  /** TipoConvocatoriaReunion service */
  private final TipoConvocatoriaReunionService service;

  /**
   * Instancia un nuevo TipoConvocatoriaReunionController.
   * 
   * @param service TipoConvocatoriaReunionService
   */
  public TipoConvocatoriaReunionController(TipoConvocatoriaReunionService service) {
    log.debug("TipoConvocatoriaReunionController(TipoConvocatoriaReunionService service) - start");
    this.service = service;
    log.debug("TipoConvocatoriaReunionController(TipoConvocatoriaReunionService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoConvocatoriaReunion}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-CNV-V', 'ETI-EVC-VR', 'ETI-EVC-VR-INV', 'ETI-EVC-EVAL', 'ETI-EVC-EVALR', 'ETI-EVC-EVALR-INV')")
  ResponseEntity<Page<TipoConvocatoriaReunion>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoConvocatoriaReunion> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoConvocatoriaReunion}.
   * 
   * @param nuevoTipoConvocatoriaReunion {@link TipoConvocatoriaReunion}. que se
   *                                     quiere crear.
   * @return Nuevo {@link TipoConvocatoriaReunion} creado.
   */
  @PostMapping
  public ResponseEntity<TipoConvocatoriaReunion> newTipoConvocatoriaReunion(
      @Valid @RequestBody TipoConvocatoriaReunion nuevoTipoConvocatoriaReunion) {
    log.debug("newTipoConvocatoriaReunion(TipoConvocatoriaReunion nuevoTipoConvocatoriaReunion) - start");
    TipoConvocatoriaReunion returnValue = service.create(nuevoTipoConvocatoriaReunion);
    log.debug("newTipoConvocatoriaReunion(TipoConvocatoriaReunion nuevoTipoConvocatoriaReunion) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoConvocatoriaReunion}.
   * 
   * @param updatedTipoConvocatoriaReunion {@link TipoConvocatoriaReunion} a
   *                                       actualizar.
   * @param id                             id {@link TipoConvocatoriaReunion} a
   *                                       actualizar.
   * @return {@link TipoConvocatoriaReunion} actualizado.
   */
  @PutMapping("/{id}")
  TipoConvocatoriaReunion replaceTipoConvocatoriaReunion(
      @Valid @RequestBody TipoConvocatoriaReunion updatedTipoConvocatoriaReunion, @PathVariable Long id) {
    log.debug(
        "replaceTipoConvocatoriaReunion(TipoConvocatoriaReunion updatedTipoConvocatoriaReunion, Long id) - start");
    updatedTipoConvocatoriaReunion.setId(id);
    TipoConvocatoriaReunion returnValue = service.update(updatedTipoConvocatoriaReunion);
    log.debug("replaceTipoConvocatoriaReunion(TipoConvocatoriaReunion updatedTipoConvocatoriaReunion, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoConvocatoriaReunion} con el id indicado.
   * 
   * @param id Identificador de {@link TipoConvocatoriaReunion}.
   * @return {@link TipoConvocatoriaReunion} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoConvocatoriaReunion one(@PathVariable Long id) {
    log.debug("TipoConvocatoriaReunion one(Long id) - start");
    TipoConvocatoriaReunion returnValue = service.findById(id);
    log.debug("TipoConvocatoriaReunion one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoConvocatoriaReunion} con id indicado.
   * 
   * @param id Identificador de {@link TipoConvocatoriaReunion}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoConvocatoriaReunion tipoConvocatoriaReunion = this.one(id);
    tipoConvocatoriaReunion.setActivo(Boolean.FALSE);
    service.update(tipoConvocatoriaReunion);
    log.debug("delete(Long id) - end");
  }

}
