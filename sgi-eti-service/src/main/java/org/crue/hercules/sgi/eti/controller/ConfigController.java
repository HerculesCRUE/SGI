package org.crue.hercules.sgi.eti.controller;

import java.util.TimeZone;

import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * ConfigController
 */
@RestController
@RequestMapping(ConfigController.MAPPING)
@Slf4j
public class ConfigController {
  public static final String MAPPING = "/config";

  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Instancia un nuevo ConfigController.
   * 
   * @param sgiConfigProperties {@link SgiConfigProperties}
   */
  public ConfigController(SgiConfigProperties sgiConfigProperties) {
    this.sgiConfigProperties = sgiConfigProperties;
  }

  /**
   * Devuelve el identificador de {@link TimeZone} configurado en la aplicación.
   * 
   * @return {@link String} con el identificador de {@link TimeZone} configurado.
   */
  @GetMapping(value = "/time-zone", produces = MediaType.TEXT_PLAIN_VALUE)
  public String timeZone() {
    log.debug("timeZone() - start");
    String returnValue = sgiConfigProperties.getTimeZone().getID();
    log.debug("timeZone() - end");
    return returnValue;
  }
}