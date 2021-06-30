package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.RequisitoIP;
import org.crue.hercules.sgi.csp.service.RequisitoIPService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * RequisitoIPController
 */
@RestController
@RequestMapping("/convocatoria-requisitoips")
@Slf4j
public class RequisitoIPController {

  /** RequisitoIP service */
  private final RequisitoIPService service;

  /**
   * Instancia un nuevo RequisitoIPController.
   * 
   * @param service {@link RequisitoIPService}
   */
  public RequisitoIPController(RequisitoIPService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link RequisitoIP}.
   * 
   * @param requisitoIP {@link RequisitoIP} que se quiere crear.
   * @return Nuevo {@link RequisitoIP} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C', 'CSP-CON-E')")
  ResponseEntity<RequisitoIP> create(@Valid @RequestBody RequisitoIP requisitoIP) {
    log.debug("create(RequisitoIP requisitoIP) - start");
    RequisitoIP returnValue = service.create(requisitoIP);
    log.debug("create(RequisitoIP requisitoIP) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RequisitoIP} con el id indicado.
   * 
   * @param requisitoIP {@link RequisitoIP} a actualizar.
   * @param id          identificador de la {@link Convocatoria} a actualizar.
   * @return {@link RequisitoIP} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  RequisitoIP update(@Validated({ Update.class, Default.class }) @RequestBody RequisitoIP requisitoIP,
      @PathVariable Long id) {
    log.debug("update(RequisitoIP requisitoIP, Long id) - start");
    RequisitoIP returnValue = service.update(requisitoIP, id);
    log.debug("update(RequisitoIP requisitoIP, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link RequisitoIP} de la {@link Convocatoria}.
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return el {@link RequisitoIP}
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<RequisitoIP> findByConvocatoriaId(@PathVariable Long id) {
    log.debug("RequisitoIP findByConvocatoriaId(Long id) - start");
    RequisitoIP returnValue = service.findByConvocatoria(id);

    if (returnValue == null) {
      log.debug("RequisitoIP findByConvocatoriaId(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("RequisitoIP findByConvocatoriaId(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

}
