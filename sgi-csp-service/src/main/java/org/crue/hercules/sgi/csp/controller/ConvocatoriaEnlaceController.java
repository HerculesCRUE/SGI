package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEnlace;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEnlaceService;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConvocatoriaEnlaceController
 */
@RestController
@RequestMapping("/convocatoriaenlaces")
@Slf4j
public class ConvocatoriaEnlaceController {

  /** ConvocatoriaEnlace service */
  private final ConvocatoriaEnlaceService service;

  /**
   * Instancia un nuevo ConvocatoriaEnlaceController.
   * 
   * @param service {@link ConvocatoriaEnlaceService}
   */
  public ConvocatoriaEnlaceController(ConvocatoriaEnlaceService service) {
    this.service = service;
  }

  /**
   * Devuelve el {@link ConvocatoriaEnlace} con el id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaEnlace}.
   * @return {@link ConvocatoriaEnlace} correspondiente al id.
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
  public ConvocatoriaEnlace findById(@PathVariable Long id) {
    log.debug("findById(Long id) - start");
    ConvocatoriaEnlace returnValue = service.findById(id);
    log.debug("findById(Long id) - end");
    return returnValue;
  }

  /**
   * Crea un nuevo {@link ConvocatoriaEnlace}.
   * 
   * @param convocatoriaEnlace {@link ConvocatoriaEnlace} que se quiere crear.
   * @return Nuevo {@link ConvocatoriaEnlace} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  public ResponseEntity<ConvocatoriaEnlace> create(@Valid @RequestBody ConvocatoriaEnlace convocatoriaEnlace) {
    log.debug("create(ConvocatoriaEnlace convocatoriaEnlace) - start");
    ConvocatoriaEnlace returnValue = service.create(convocatoriaEnlace);
    log.debug("create(ConvocatoriaEnlace convocatoriaEnlace) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link ConvocatoriaEnlace} con el id indicado.
   * 
   * @param convocatoriaEnlace {@link ConvocatoriaEnlace} a actualizar.
   * @param id                 id {@link ConvocatoriaEnlace} a actualizar.
   * @return {@link ConvocatoriaEnlace} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public ConvocatoriaEnlace update(
      @Validated({ Update.class, Default.class }) @RequestBody ConvocatoriaEnlace convocatoriaEnlace,
      @PathVariable Long id) {
    log.debug("update(ConvocatoriaEnlace convocatoriaEnlace, Long id) - start");
    convocatoriaEnlace.setId(id);
    ConvocatoriaEnlace returnValue = service.update(convocatoriaEnlace);
    log.debug("update(ConvocatoriaEnlace convocatoriaEnlace, Long id) - end");
    return returnValue;
  }

  /**
   * Desactiva el {@link ConvocatoriaEnlace} con id indicado.
   * 
   * @param id Identificador de {@link ConvocatoriaEnlace}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  public void deleteById(@PathVariable Long id) {
    log.debug("deleteById(Long id) - start");
    service.delete(id);
    log.debug("deleteById(Long id) - end");
  }

}
