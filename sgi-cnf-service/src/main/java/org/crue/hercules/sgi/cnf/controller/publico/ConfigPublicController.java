package org.crue.hercules.sgi.cnf.controller.publico;

import org.crue.hercules.sgi.cnf.converter.ConfigConverter;
import org.crue.hercules.sgi.cnf.dto.ConfigOutput;
import org.crue.hercules.sgi.cnf.model.Config;
import org.crue.hercules.sgi.cnf.service.ConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@link Config} data.
 */
@RestController
@RequestMapping(ConfigPublicController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigPublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "config";

  public static final String PATH_NAME = PATH_DELIMITER + "{name}";

  private final ConfigService service;
  private final ConfigConverter converter;

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

}
