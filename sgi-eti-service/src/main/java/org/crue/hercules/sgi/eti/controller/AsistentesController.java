package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.Asistentes;
import org.crue.hercules.sgi.eti.service.AsistentesService;
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
 * AsistentesController
 */
@RestController
@RequestMapping("/asistentes")
@Slf4j
public class AsistentesController {

  /** Asistentes service */
  private final AsistentesService service;

  /**
   * Instancia un nuevo AsistentesController.
   * 
   * @param service AsistentesService
   */
  public AsistentesController(AsistentesService service) {
    log.debug("AsistentesController(AsistentesService service) - start");
    this.service = service;
    log.debug("AsistentesController(AsistentesService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link Asistentes}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ASISTENTES-VER')")
  ResponseEntity<Page<Asistentes>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Asistentes> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link Asistentes}.
   * 
   * @param nuevoAsistentes {@link Asistentes}. que se quiere crear.
   * @return Nuevo {@link Asistentes} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('ETI-CNV-C')")
  public ResponseEntity<Asistentes> newAsistentes(@Valid @RequestBody Asistentes nuevoAsistentes) {
    log.debug("newAsistentes(Asistentes nuevoAsistentes) - start");
    Asistentes returnValue = service.create(nuevoAsistentes);
    log.debug("newAsistentes(Asistentes nuevoAsistentes) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link Asistentes}.
   * 
   * @param updatedAsistentes {@link Asistentes} a actualizar.
   * @param id                id {@link Asistentes} a actualizar.
   * @return {@link Asistentes} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('ETI-ACT-C', 'ETI-ACT-E')")
  Asistentes replaceAsistentes(@Valid @RequestBody Asistentes updatedAsistentes, @PathVariable Long id) {
    log.debug("replaceAsistentes(Asistentes updatedAsistentes, Long id) - start");
    updatedAsistentes.setId(id);
    Asistentes returnValue = service.update(updatedAsistentes);
    log.debug("replaceAsistentes(Asistentes updatedAsistentes, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link Asistentes} con el id indicado.
   * 
   * @param id Identificador de {@link Asistentes}.
   * @return {@link Asistentes} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ASISTENTES-VER')")
  Asistentes one(@PathVariable Long id) {
    log.debug("Asistentes one(Long id) - start");
    Asistentes returnValue = service.findById(id);
    log.debug("Asistentes one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link Asistentes} con id indicado.
   * 
   * @param id Identificador de {@link Asistentes}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('ETI-ASISTENTES-EDITAR')")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

}
