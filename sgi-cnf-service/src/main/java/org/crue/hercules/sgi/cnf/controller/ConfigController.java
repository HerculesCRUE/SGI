package org.crue.hercules.sgi.cnf.controller;

import java.util.TimeZone;

import javax.validation.Valid;

import org.crue.hercules.sgi.cnf.config.SgiConfigProperties;
import org.crue.hercules.sgi.cnf.converter.ConfigConverter;
import org.crue.hercules.sgi.cnf.dto.ConfigOutput;
import org.crue.hercules.sgi.cnf.dto.CreateConfigInput;
import org.crue.hercules.sgi.cnf.dto.UpdateConfigInput;
import org.crue.hercules.sgi.cnf.model.Config;
import org.crue.hercules.sgi.cnf.service.ConfigService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@link Config} data.
 */
@RestController
@RequestMapping(ConfigController.MAPPING)
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

  private final ConfigConverter converter;
  private final SgiConfigProperties sgiConfigProperties;
  private final ConfigService service;

  /**
   * Creates {@link ConfigController}.
   * 
   * @param sgiConfigProperties {@link SgiConfigProperties}
   * @param converter           {@link ConfigConverter}
   * @param configService       {@link ConfigService}
   */
  public ConfigController(SgiConfigProperties sgiConfigProperties, ConfigConverter converter,
      ConfigService configService) {
    log.debug("ConfigController(SgiConfigProperties sgiConfigProperties, ConfigService configService) - start");
    this.sgiConfigProperties = sgiConfigProperties;
    this.converter = converter;
    this.service = configService;
    log.debug("ConfigController(SgiConfigProperties sgiConfigProperties, ConfigService configService) - end");
  }

  /**
   * Get the application configured {@link TimeZone} identifier.
   * 
   * @return {@link String} the identifier
   */
  @GetMapping(value = "/time-zone", produces = MediaType.TEXT_PLAIN_VALUE)
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<String> timeZone() {
    log.debug("timeZone() - start");
    String returnValue = sgiConfigProperties.getTimeZone().getID();
    log.debug("timeZone() - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Get {@link Config}.
   * 
   * @param name the {@link Config} name
   * @return ConfigOutput the {@link Config}
   */
  @GetMapping(PATH_NAME)
  public ResponseEntity<ConfigOutput> get(@PathVariable String name) {
    log.debug("getById(@PathVariable Long id) - start");
    Config config = service.get(name);
    ConfigOutput returnValue = converter.convert(config);
    log.debug("getById(@PathVariable Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Get paged and filtered {@link Config} data.
   * 
   * @param query  query filter
   * @param paging pagging info
   * @return the requested data
   */
  @GetMapping()
  public ResponseEntity<Page<ConfigOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Config> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    Page<ConfigOutput> returnValue = converter.convert(page);
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Create {@link Config}.
   * 
   * @param config {@link Config} to create
   * @return the newly created {@link Config}
   */
  @PostMapping
  public ResponseEntity<ConfigOutput> create(@Valid @RequestBody CreateConfigInput config) {
    log.debug("create(ChecklistInput checklist) - start");
    Config created = service.create(converter.convert(config));
    ConfigOutput returnValue = converter.convert(created);
    log.debug("create(ChecklistInput checklist) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Update existing {@link Config}.
   * 
   * @param name   {@link Config} name
   * @param config {@link Config} data
   * @return the updated {@link Config}
   */
  @PatchMapping(PATH_NAME)
  public ResponseEntity<ConfigOutput> update(@PathVariable String name, @Valid @RequestBody UpdateConfigInput config) {
    log.debug("update(Long id, String respuesta) - start");
    Config updated = service.update(converter.convert(name, config));
    ConfigOutput returnValue = converter.convert(updated);
    log.debug("update(Long id, String respuesta) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Delete existing {@link Config}.
   * 
   * @param name {@link Config} name
   */
  @DeleteMapping(PATH_NAME)
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String name) {
    log.debug("delete(String name) - start");
    service.delete(name);
    log.debug("delete(String name) - end");
  }

}