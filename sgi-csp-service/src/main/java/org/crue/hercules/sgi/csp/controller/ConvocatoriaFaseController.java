package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.crue.hercules.sgi.csp.service.ConvocatoriaFaseService;
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
 * ConvocatoriaFaseController
 */
@RestController
@RequestMapping("/convocatoriafases")
@Slf4j
public class ConvocatoriaFaseController {

  /** ConvocatoriaFase service */
  private final ConvocatoriaFaseService service;

  /**
   * Instancia un nuevo ConvocatoriaFaseController.
   * 
   * @param service {@link ConvocatoriaFaseService}
   */
  public ConvocatoriaFaseController(ConvocatoriaFaseService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link ConvocatoriaFase} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaFase}.
   * @return {@link ConvocatoriaFase} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaFase findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaFase returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ConvocatoriaFase}.
   * 
   * @param convocatoriaFase {@link ConvocatoriaFase} que se quiere crear.
   * @return Nuevo {@link ConvocatoriaFase} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C','CSP-CON-E')")
  public ResponseEntity<ConvocatoriaFase> create(@Valid @RequestBody ConvocatoriaFase convocatoriaFase) {
    log.debug("create(ConvocatoriaFase convocatoriaFase) - start");
    ConvocatoriaFase returnValue = service.create(convocatoriaFase);
    log.debug("create(ConvocatoriaFase convocatoriaFase) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaFase} con el id indicado.
   * 
   * @param convocatoriaFase {@link ConvocatoriaFase} a actualizar.
   * @param id               id {@link ConvocatoriaFase} a actualizar.
   * @return {@link ConvocatoriaFase} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaFase update(
      @Validated({ Update.class, Default.class }) @RequestBody ConvocatoriaFase convocatoriaFase,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaFase convocatoriaFase, Long id) - start");
    convocatoriaFase.setId(id);
    ConvocatoriaFase returnValue = service.update(convocatoriaFase);
    log.debug("update(ConvocatoriaFase convocatoriaFase, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConvocatoriaFase} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaFase}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
