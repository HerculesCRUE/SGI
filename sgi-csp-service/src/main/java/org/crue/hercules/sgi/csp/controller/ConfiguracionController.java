package org.crue.hercules.sgi.csp.controller;

import javax.validation.Valid;
import org.crue.hercules.sgi.csp.model.Configuracion;
import org.crue.hercules.sgi.csp.service.ConfiguracionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

/**
 * ConfiguracionController
 */
@RestController
@RequestMapping("/configuraciones")
@Slf4j
public class ConfiguracionController {

  /** Configuracion service */
  private final ConfiguracionService service;

  /**
   * Instancia un nuevo ConfiguracionController.
   * 
   * @param service ConfiguracionService
   */
  public ConfiguracionController(ConfiguracionService service) {
    log.debug("ConfiguracionController(ConfiguracionService service) - start");
    this.service = service;
    log.debug("ConfiguracionController(ConfiguracionService service) - end");
  }

  /**
   * Devuelve la {@link Configuracion}.
   * 
   * @return el objeto {@link Configuracion}
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E')")
  public ResponseEntity<Configuracion> findConfiguracion() {
    log.debug("findConfiguracion() - start");
    Configuracion configuracion = service.findConfiguracion();
    log.debug("findConfiguracion() - end");
    return new ResponseEntity<>(configuracion, HttpStatus.OK);
  }

  /**
   * Actualiza {@link Configuracion}.
   * 
   * @param updatedConfiguracion {@link Configuracion} a actualizar.
   * @param id                   id {@link Configuracion} a actualizar.
   * @return {@link Configuracion} actualizado.
   */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CNF-E')")
  public Configuracion replaceConfiguracion(@Valid @RequestBody Configuracion updatedConfiguracion,
      @PathVariable Long id) {
    log.debug("replaceConfiguracion(Configuracion updatedConfiguracion, Long id) - start");
    updatedConfiguracion.setId(id);
    Configuracion returnValue = service.update(updatedConfiguracion);
    log.debug("replaceConfiguracion(Configuracion updatedConfiguracion, Long id) - end");
    return returnValue;
  }

}
