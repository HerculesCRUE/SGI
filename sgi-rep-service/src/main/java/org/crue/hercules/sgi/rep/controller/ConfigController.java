package org.crue.hercules.sgi.rep.controller;

import java.util.TimeZone;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
  /** The URL path delimiter */
  public static final String PATH_DELIMITER = "/";
  /** The controller base path mapping */
  public static final String MAPPING = PATH_DELIMITER + "config";
  /** The path to request the Time Zone */
  public static final String PATH_TIMEZONE = PATH_DELIMITER + "time-zone";

  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Instancia un nuevo ConfigController.
   * 
   * @param sgiConfigProperties {@link SgiConfigProperties}
   */
  public ConfigController(SgiConfigProperties sgiConfigProperties) {
    log.debug("ConfigController(SgiConfigProperties sgiConfigProperties) - start");
    this.sgiConfigProperties = sgiConfigProperties;
    log.debug("ConfigController(SgiConfigProperties sgiConfigProperties) - end");
  }

  /**
   * Devuelve el identificador de {@link TimeZone} configurado en la aplicación.
   * 
   * @return {@link String} con el identificador de {@link TimeZone} configurado.
   */
  @GetMapping(value = PATH_TIMEZONE, produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> timeZone() {
    log.debug("timeZone() - start");
    String returnValue = sgiConfigProperties.getTimeZone().getID();
    log.debug("timeZone() - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }
}