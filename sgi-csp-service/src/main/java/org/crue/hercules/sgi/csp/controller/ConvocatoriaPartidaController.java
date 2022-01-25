package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.service.ConvocatoriaPartidaService;
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
 * ConvocatoriaPartidaController
 */
@RestController
@RequestMapping("/convocatoria-partidas")
@Slf4j
public class ConvocatoriaPartidaController {

  /** ConvocatoriaPartida service */
  private final ConvocatoriaPartidaService service;

  /**
   * Instancia un nuevo ConvocatoriaPartidaController.
   * 
   * @param service {@link ConvocatoriaPartidaService}
   */
  public ConvocatoriaPartidaController(ConvocatoriaPartidaService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link ConvocatoriaPartida} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaPartida}.
   * @return {@link ConvocatoriaPartida} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaPartida findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaPartida returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ConvocatoriaPartida}.
   * 
   * @param convocatoriaPartida {@link ConvocatoriaPartida} que se quiere crear.
   * @return Nuevo {@link ConvocatoriaPartida} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<ConvocatoriaPartida> create(@Valid @RequestBody ConvocatoriaPartida convocatoriaPartida) {
    log.debug("create(ConvocatoriaPartida convocatoriaPartida) - start");
    ConvocatoriaPartida returnValue = service.create(convocatoriaPartida);
    log.debug("create(ConvocatoriaPartida convocatoriaPartida) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaPartida} con el id indicado.
   * 
   * @param convocatoriaPartida {@link ConvocatoriaPartida} a actualizar.
   * @param id                  id {@link ConvocatoriaPartida} a actualizar.
   * @return {@link ConvocatoriaPartida} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaPartida update(
      @Validated({ Update.class, Default.class }) @RequestBody ConvocatoriaPartida convocatoriaPartida,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaPartida convocatoriaPartida, Long id) - start");
    convocatoriaPartida.setId(id);
    ConvocatoriaPartida returnValue = service.update(convocatoriaPartida);
    log.debug("update(ConvocatoriaPartida convocatoriaPartida, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConvocatoriaPartida} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaPartida}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaPartida} puede ser modificada.
   * 
   * @param id Id de la {@link ConvocatoriaPartida}.
   * @return HTTP-200 Si se permite modificación / HTTP-204 Si no se permite
   *         modificación
   */
  @RequestMapping(path = "/{id}/modificable", method = RequestMethod.HEAD)
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V')")
  public ResponseEntity<ConvocatoriaPartida> modificable(@PathVariable Long id) {
    log.debug("modificable(Long id) - start");
    boolean returnValue = service.modificable(id, "CSP-CON-E");
    log.debug("modificable(Long id) - end");
    return returnValue ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
