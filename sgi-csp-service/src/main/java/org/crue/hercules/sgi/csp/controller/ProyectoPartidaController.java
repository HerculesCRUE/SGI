package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.AnualidadGasto;
import org.crue.hercules.sgi.csp.model.AnualidadIngreso;
import org.crue.hercules.sgi.csp.model.ProyectoPartida;
import org.crue.hercules.sgi.csp.service.ProyectoPartidaService;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ProyectoPartidaController
 */
@RestController
@RequestMapping("/proyecto-partidas")
@Slf4j
public class ProyectoPartidaController {

  /** ProyectoPartida service */
  private final ProyectoPartidaService service;

  /**
   * Instancia un nuevo ProyectoPartidaController.
   * 
   * @param service {@link ProyectoPartidaService}.
   */
  public ProyectoPartidaController(ProyectoPartidaService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link ProyectoPartida}.
   * 
   * @param proyectoPartida {@link ProyectoPartida} que se quiere crear.
   * @return Nuevo {@link ProyectoPartida} creado.
   */
  @PostMapping
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ResponseEntity<ProyectoPartida> create(@Valid @RequestBody ProyectoPartida proyectoPartida) {
    log.debug("create(ProyectoPartida proyectoPartida) - start");
    ProyectoPartida returnValue = service.create(proyectoPartida);
    log.debug("create(ProyectoPartida proyectoPartida) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ProyectoPartida} con el id indicado.
   * 
   * @param proyectoPartida {@link ProyectoPartida} a actualizar.
   * @param id              id {@link ProyectoPartida} a actualizar.
   * @return {@link ProyectoPartida} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoPartida update(@Validated({ Update.class, Default.class }) @RequestBody ProyectoPartida proyectoPartida,
      @PathVariable Long id) {
    log.debug("update(ProyectoPartida proyectoPartida, Long id) - start");
    proyectoPartida.setId(id);
    ProyectoPartida returnValue = service.update(proyectoPartida);
    log.debug("update(ProyectoPartida proyectoPartida, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ProyectoPartida} con id indicado.
   * 
   * @param id Identificador de {@link ProyectoPartida}.
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
   * Comprueba la existencia del {@link ProyectoPartida} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoPartida}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<?> exists(@PathVariable Long id) {
    log.debug("exists(Long id) - start");
    if (service.existsById(id)) {
      log.debug("exists(Long id) - end");
      return new ResponseEntity<>(HttpStatus.OK);
    }
    log.debug("exists(Long id) - end");
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Devuelve el {@link ProyectoPartida} con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoPartida}.
   * @return {@link ProyectoPartida} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  ProyectoPartida findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ProyectoPartida returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ProyectoPartida} puede ser modificada.
   * 
   * @param id Id de la {@link ProyectoPartida}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = "/{id}/modificable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-PRO-V')")
  ResponseEntity<ProyectoPartida> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    boolean returnValue = service.modificable(id, "CSP-PRO-E");
    log.debug("modificable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Comprueba si algún objeto de tipo {@link AnualidadIngreso} o de tipo
   * {@link AnualidadGasto} está asociado a la partida con el id indicado.
   * 
   * @param id Identificador de {@link ProyectoPartida}.
   * @return HTTP 200 si existe y HTTP 204 si no.
   */
  @RequestMapping(path = "/{id}/anualidades", method = RequestMethod.HEAD)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Object> existsAnyAnualidad(@PathVariable Long id) {

    if (service.existsAnyAnualidad(id)) {
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
