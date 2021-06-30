package org.crue.hercules.sgi.eti.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.eti.model.CargoComite;
import org.crue.hercules.sgi.eti.service.CargoComiteService;
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
 * CargoComiteController
 */
@RestController
@RequestMapping("/cargocomites")
@Slf4j
public class CargoComiteController {

  /** CargoComite service */
  private final CargoComiteService service;

  /**
   * Instancia un nuevo CargoComiteController.
   * 
   * @param service CargoComiteService
   */
  public CargoComiteController(CargoComiteService service) {
    log.debug("CargoComiteController(CargoComiteService service) - start");
    this.service = service;
    log.debug("CargoComiteController(CargoComiteService service) - end");
  }

  /**
   * Devuelve una lista paginada y filtrada {@link CargoComite}.
   * 
   * @param query  filtro de búsqueda.
   * @param paging pageable
   */
  @GetMapping()
  ResponseEntity<Page<CargoComite>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<CargoComite> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(page, HttpStatus.OK);
  }

  /**
   * Crea nuevo {@link CargoComite}.
   * 
   * @param nuevoCargoComite {@link CargoComite}. que se quiere crear.
   * @return Nuevo {@link CargoComite} creado.
   */
  @PostMapping
  public ResponseEntity<CargoComite> newCargoComite(@Valid @RequestBody CargoComite nuevoCargoComite) {
    log.debug("newCargoComite(CargoComite nuevoCargoComite) - start");
    CargoComite returnValue = service.create(nuevoCargoComite);
    log.debug("newCargoComite(CargoComite nuevoCargoComite) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza {@link CargoComite}.
   * 
   * @param updatedCargoComite {@link CargoComite} a actualizar.
   * @param id                 id {@link CargoComite} a actualizar.
   * @return {@link CargoComite} actualizado.
   */
  @PutMapping("/{id}")
  CargoComite replaceCargoComite(@Valid @RequestBody CargoComite updatedCargoComite, @PathVariable Long id) {
    log.debug("replaceCargoComite(CargoComite updatedCargoComite, Long id) - start");
    updatedCargoComite.setId(id);
    CargoComite returnValue = service.update(updatedCargoComite);
    log.debug("replaceCargoComite(CargoComite updatedCargoComite, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link CargoComite} con el id indicado.
   * 
   * @param id Identificador de {@link CargoComite}.
   * @return {@link CargoComite} correspondiente al id.
   */
  @GetMapping("/{id}")
  CargoComite one(@PathVariable Long id) {
    log.debug("CargoComite one(Long id) - start");
    CargoComite returnValue = service.findById(id);
    log.debug("CargoComite one(Long id) - end");
    return returnValue;
  }

  /**
   * Elimina {@link CargoComite} con id indicado.
   * 
   * @param id Identificador de {@link CargoComite}.
   */
  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    CargoComite cargoComite = this.one(id);
    cargoComite.setActivo(Boolean.FALSE);
    service.update(cargoComite);
    log.debug("delete(Long id) - end");
  }

}
