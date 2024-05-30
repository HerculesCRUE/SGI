package org.crue.hercules.sgi.csp.controller;

import java.util.TimeZone;

import javax.validation.Valid;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.ConfigParamOutput;
import org.crue.hercules.sgi.csp.model.Configuracion;
import org.crue.hercules.sgi.csp.service.ConfiguracionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ConfigController
 */
@RestController
@RequestMapping(ConfigController.MAPPING)
@RequiredArgsConstructor
@Slf4j
public class ConfigController {
  /** The URL path delimiter */
  public static final String PATH_DELIMITER = "/";
  /** The controller base path mapping */
  public static final String MAPPING = PATH_DELIMITER + "config";
  /** The path used to request by name */
  public static final String PATH_NAME = PATH_DELIMITER + "{name}";
  /** The path to request the Time Zone */
  public static final String PATH_TIMEZONE = PATH_DELIMITER + "time-zone";

  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  private final ConfiguracionService service;
  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Devuelve el identificador de {@link TimeZone} configurado en la aplicación.
   * 
   * @return {@link String} con el identificador de {@link TimeZone} configurado.
   */
  @GetMapping(value = PATH_TIMEZONE, produces = MediaType.TEXT_PLAIN_VALUE)
  public String timeZone() {
    log.debug("timeZone() - start");
    String returnValue = sgiConfigProperties.getTimeZone().getID();
    log.debug("timeZone() - end");
    return returnValue;
  }

  /**
   * Devuelve la {@link Configuracion}.
   * 
   * @return el objeto {@link Configuracion}
   */
  @GetMapping()
  @PreAuthorize("hasAnyAuthorityForAnyUO('CSP-PRO-E', 'CSP-EJEC-V', 'CSP-EJEC-E', 'CSP-PRO-INV-VR', 'CSP-PRO-INV-ER')")
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
  @PutMapping(PATH_ID)
  @PreAuthorize("hasAuthorityForAnyUO('CSP-CNF-E')")
  public Configuracion replaceConfiguracion(@Valid @RequestBody Configuracion updatedConfiguracion,
      @PathVariable Long id) {
    log.debug("replaceConfiguracion(Configuracion updatedConfiguracion, Long id) - start");
    updatedConfiguracion.setId(id);
    Configuracion returnValue = service.update(updatedConfiguracion);
    log.debug("replaceConfiguracion(Configuracion updatedConfiguracion, Long id) - end");
    return returnValue;
  }

  /**
   * Get {@link ConfigParamOutput}.
   * 
   * @param name the {@link ConfigParamOutput} name
   * @return ConfigOutput the {@link ConfigParamOutput}
   */
  @GetMapping(PATH_NAME)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAnyAuthorityForAnyUO('ADM-CNF-E', 'CSP-EJEC-V', 'CSP-EJEC-E', 'CSP-EJEC-INV-VR', 'CSP-GIN-E', 'CSP-GIN-V', 'CSP-GIN-INV-VR', 'CSP-PRO-E', 'CSP-PRO-INV-VR', 'CSP-PRO-INV-ER', 'CSP-CON-E', 'CSP-CON-V', 'CSP-CON-INV-V')")
  public ResponseEntity<ConfigParamOutput> get(@PathVariable String name) {
    log.debug("getById(@PathVariable Long id) - start");
    ConfigParamOutput returnValue = service.get(name);
    log.debug("getById(@PathVariable Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Update existing {@link ConfigParamOutput} value.
   * 
   * @param name  {@link ConfigParamOutput} name
   * @param value {@link ConfigParamOutput} value
   * @return the updated {@link ConfigParamOutput}
   */
  @PatchMapping(PATH_NAME)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ResponseEntity<ConfigParamOutput> updateValue(@PathVariable String name,
      @Valid @RequestBody(required = false) String value) {
    log.debug("updateValue(String name, String value) - start");
    ConfigParamOutput returnValue = service.updateValue(name, value);
    log.debug("updateValue(String name, String value) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

}