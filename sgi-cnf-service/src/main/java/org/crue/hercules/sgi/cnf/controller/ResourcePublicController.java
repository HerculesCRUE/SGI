package org.crue.hercules.sgi.cnf.controller;

import java.util.concurrent.TimeUnit;

import org.crue.hercules.sgi.cnf.config.SgiConfigProperties;
import org.crue.hercules.sgi.cnf.dto.ResourceLastModifiedDateOutput;
import org.crue.hercules.sgi.cnf.dto.ResourceValueOutput;
import org.crue.hercules.sgi.cnf.model.Resource;
import org.crue.hercules.sgi.cnf.service.ResourceService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@link Resource} data.
 */
@RestController
@RequestMapping(ResourcePublicController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
@Component
public class ResourcePublicController {

  public static final String PATH_DELIMITER = "/";
  public static final String PATH_PUBLIC = PATH_DELIMITER + "public";
  public static final String REQUEST_MAPPING = PATH_PUBLIC + PATH_DELIMITER + "resources";

  public static final String PATH_NAME = PATH_DELIMITER + "{name}";

  private final ResourceService service;
  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Get {@link Resource} value.
   * 
   * @param name the {@link Resource} name
   * @return the {@link Resource} value
   */
  @GetMapping(PATH_NAME)
  public ResponseEntity<byte[]> getResource(@PathVariable String name) {
    log.debug("getResource({}) - start", name);

    CacheControl cacheControl = CacheControl.maxAge(sgiConfigProperties.getResourcesCacheMaxAge(), TimeUnit.SECONDS)
        .noTransform()
        .mustRevalidate();

    ResourceValueOutput returnValue = service.getPublicResourceValue(name);
    log.debug("getResource({}) - end", name);

    return ResponseEntity.ok().cacheControl(cacheControl).lastModified(returnValue.getLastModifiedDate())
        .body(returnValue.getValue());
  }

  /**
   * Get {@link Resource} last modified date header.
   * 
   * @param name the {@link Resource} name
   * @return the {@link Resource} value
   */
  @RequestMapping(path = PATH_NAME, method = RequestMethod.HEAD)
  public ResponseEntity<Void> getResourceLastModifiedDate(@PathVariable String name) {
    log.debug("getResourceLastModifiedDate({}) - start", name);
    ResourceLastModifiedDateOutput returnValue = service.getPublicResourceLastModifiedDate(name);
    log.debug("getResourceLastModifiedDate({}) - end", name);

    return ResponseEntity.ok().lastModified(returnValue.getLastModifiedDate()).body(null);
  }

}
