package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.service.TipoActividadService;
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
 * TipoActividadController
 */
@RestController
@RequestMapping("/tipoactividades")
@Slf4j
public class TipoActividadController {

  /** TipoActividad service */
  private final TipoActividadService service;

  /**
   * Instancia un nuevo TipoActividadController.
   * 
   * @param service TipoActividadService
   */
  public TipoActividadController(TipoActividadService service) {
    log.debug("TipoActividadController(TipoActividadService service) - start");
    this.service = service;
    log.debug("TipoActividadController(TipoActividadService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link TipoActividad}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-PEV-C-INV', 'ETI-PEV-ER-INV', 'ETI-PEV-V')")
  ResponseEntity<Page<TipoActividad>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<TipoActividad> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link TipoActividad}.
   * 
   * @param nuevoTipoActividad {@link TipoActividad}. que se quiere crear.
   * @return Nuevo {@link TipoActividad} creado.
   */
  @PostMapping
  ResponseEntity<TipoActividad> newTipoActividad(@Valid @RequestBody TipoActividad nuevoTipoActividad) {
    log.debug("newTipoActividad(TipoActividad nuevoTipoActividad) - start");
    TipoActividad returnValue = service.create(nuevoTipoActividad);
    log.debug("newTipoActividad(TipoActividad nuevoTipoActividad) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link TipoActividad}.
   * 
   * @param updatedTipoActividad {@link TipoActividad} a actualizar.
   * @param id                   id {@link TipoActividad} a actualizar.
   * @return {@link TipoActividad} actualizado.
   */
  @PutMapping("/{id}")
  TipoActividad replaceTipoActividad(@Valid @RequestBody TipoActividad updatedTipoActividad, @PathVariable Long id) {
    log.debug("replaceTipoActividad(TipoActividad updatedTipoActividad, Long id) - start");
    updatedTipoActividad.setId(id);
    TipoActividad returnValue = service.update(updatedTipoActividad);
    log.debug("replaceTipoActividad(TipoActividad updatedTipoActividad, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link TipoActividad} con el id indicado.
   * 
   * @param id Identificador de {@link TipoActividad}.
   * @return {@link TipoActividad} correspondiente al id.
   */
  @GetMapping("/{id}")
  TipoActividad one(@PathVariable Long id) {
    log.debug("TipoActividad one(Long id) - start");
    TipoActividad returnValue = service.findById(id);
    log.debug("TipoActividad one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link TipoActividad} con id indicado.
   * 
   * @param id Identificador de {@link TipoActividad}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    TipoActividad tipoActividad = this.one(id);
    tipoActividad.setActivo(Boolean.FALSE);
    service.update(tipoActividad);
    log.debug("delete(Long id) - end");
  }

}
