package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.crue.hercules.sgi.csp.service.ProyectoFaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoFaseController
 */
@RestController
@RequestMapping("/proyectofases")
@Slf4j
public class ProyectoFaseController {

  /** ProyectoFase service */
  private final ProyectoFaseService service;

  /**
   * Instancia un nuevo ProyectoFaseController.
   * 
   * @param service {@link ProyectoFaseService}
   */
  public ProyectoFaseController(ProyectoFaseService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link ProyectoFase}.
   * 
   * @param proyectoFase {@link ProyectoFase} que se quiere crear.
   * @return Nuevo {@link ProyectoFase} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<ProyectoFase> create(@Valid @RequestBody ProyectoFase proyectoFase) {
    log.debug("create(ProyectoFase proyectoFase) - start");
    ProyectoFase returnValue = service.create(proyectoFase);
    log.debug("create(ProyectoFase proyectoFase) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoFase} con el id indicado.
   * 
   * @param proyectoFase {@link ProyectoFase} a actualizar.
   * @param id           id {@link ProyectoFase} a actualizar.
   * @return {@link ProyectoFase} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoFase update(@Validated({ Update.class, Default.class }) @RequestBody ProyectoFase proyectoFase,
      @PathVariable Long id) {
    log.debug("update(ProyectoFase proyectoFase, Long id) - start");
    proyectoFase.setId(id);
    ProyectoFase returnValue = service.update(proyectoFase);
    log.debug("update(ProyectoFase proyectoFase, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoFase} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoFase}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Devuelve el {@link ProyectoFase} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoFase}.
   * @return {@link ProyectoFase} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  ProyectoFase findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoFase returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

}
