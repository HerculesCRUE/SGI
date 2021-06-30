package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import javax.validation.groups.Default;

import org.crue.hercules.sgi.csp.model.BaseEntity.Update;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.RequisitoEquipo;
import org.crue.hercules.sgi.csp.service.RequisitoEquipoService;
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
 * RequisitoEquipoController
 */
@RestController
@RequestMapping("/convocatoria-requisitoequipos")
@Slf4j
public class RequisitoEquipoController {

  /** RequisitoEquipo service */
  private final RequisitoEquipoService service;

  /**
   * Instancia un nuevo RequisitoEquipoController.
   * 
   * @param service {@link RequisitoEquipoService}
   */
  public RequisitoEquipoController(RequisitoEquipoService service) {
    this.service = service;
  }

  /**
   * Crea un nuevo {@link RequisitoEquipo}.
   * 
   * @param requisitoEquipo {@link RequisitoEquipo} que se quiere crear.
   * @return Nuevo {@link RequisitoEquipo} creado.
   */
  @PostMapping
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-C','CSP-CON-E')")
  ResponseEntity<RequisitoEquipo> create(@Valid @RequestBody RequisitoEquipo requisitoEquipo) {
    log.debug("create(RequisitoEquipo requisitoEquipo) - start");
    RequisitoEquipo returnValue = service.create(requisitoEquipo);
    log.debug("create(RequisitoEquipo requisitoEquipo) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Actualiza el {@link RequisitoEquipo} con el id de {@link Convocatoria}
   * indicado.
   * 
   * @param requisitoEquipo {@link RequisitoEquipo} a actualizar.
   * @param id              Identificador de la {@link Convocatoria}.
   * @return {@link RequisitoEquipo} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CON-E')")
  RequisitoEquipo update(@Validated({ Update.class, Default.class }) @RequestBody RequisitoEquipo requisitoEquipo,
      @PathVariable Long id) {
    log.debug("update(RequisitoEquipo requisitoEquipo, Long id) - start");
    RequisitoEquipo returnValue = service.update(requisitoEquipo, id);
    log.debug("update(RequisitoEquipo requisitoEquipo, Long id) - end");
    return returnValue;
  }

  /**
   * Devuelve el {@link RequisitoEquipo} de la {@link Convocatoria}.
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return el {@link RequisitoEquipo}
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  ResponseEntity<RequisitoEquipo> findByConvocatoriaId(@PathVariable Long id) {
    log.debug("RequisitoEquipo findByConvocatoriaId(Long id) - start");
    RequisitoEquipo returnValue = service.findByConvocatoriaId(id);

    if (returnValue == null) {
      log.debug("RequisitoEquipo findByConvocatoriaId(Long id) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("RequisitoEquipo findByConvocatoriaId(Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

}
