package org.crue.hercules.sgi.cnf.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.crue.hercules.sgi.cnf.config.SgiConfigProperties;
import org.crue.hercules.sgi.cnf.dto.ResourceInfoOutput;
import org.crue.hercules.sgi.cnf.dto.ResourceLastModifiedDateOutput;
import org.crue.hercules.sgi.cnf.dto.ResourceValueOutput;
import org.crue.hercules.sgi.cnf.model.Resource;
import org.crue.hercules.sgi.cnf.service.ResourceService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@link Resource} data.
 */
@RestController
@RequestMapping(ResourceController.REQUEST_MAPPING)
@Slf4j
@RequiredArgsConstructor
public class ResourceController {

  public static final String PATH_DELIMITER = "/";
  public static final String REQUEST_MAPPING = PATH_DELIMITER + "resources";

  public static final String PATH_NAME = PATH_DELIMITER + "{name}";
  public static final String PATH_INFO = PATH_NAME + PATH_DELIMITER + "info";
  public static final String PATH_RESTORE = PATH_NAME + PATH_DELIMITER + "restore";

  private final ResourceService service;
  private final SgiConfigProperties sgiConfigProperties;

  /**
   * Get {@link Resource} value.
   * 
   * @param name the {@link Resource} name
   * @return the {@link Resource} value
   */
  @GetMapping(PATH_NAME)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ResponseEntity<byte[]> getResource(@PathVariable String name) {
    log.debug("getResource({}) - start", name);

    CacheControl cacheControl = CacheControl.maxAge(sgiConfigProperties.getResourcesCacheMaxAge(), TimeUnit.SECONDS)
        .noTransform()
        .mustRevalidate();

    ResourceValueOutput returnValue = service.getValue(name);
    log.debug("getResource({}) - end", name);

    return ResponseEntity.ok().cacheControl(cacheControl).lastModified(returnValue.getLastModifiedDate())
        .body(returnValue.getValue());
  }

  /**
   * Get {@link Resource} info.
   * 
   * @param name the {@link Resource} name
   * @return the {@link Resource} info
   */
  @GetMapping(PATH_INFO)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ResponseEntity<ResourceInfoOutput> getResourceInfo(@PathVariable String name) {
    log.debug("getResourceInfo({}) - start", name);
    ResourceInfoOutput returnValue = service.getInfo(name);
    log.debug("getResourceInfo({}) - end", name);
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Update existing {@link Resource} value.
   * 
   * @param name {@link Resource} name
   * @param file {@link Resource} value
   * @return HTTP 200
   */
  @PatchMapping(PATH_NAME)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ResponseEntity<Void> updateValue(@PathVariable String name, @RequestPart("file") MultipartFile file) {
    log.debug("updateValue({}, MultipartFile file) - start", name);

    try {
      service.updateValue(name, file.getBytes());
    } catch (IOException e) {
      log.error("updateValue({}, MultipartFile file)", e);
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    log.debug("updateValue({}, MultipartFile file) - end", name);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Restore the default value of an existing {@link Resource}
   * 
   * @param name {@link Resource} name
   * @return HTTP 200
   */
  @PatchMapping(PATH_RESTORE)
  @PreAuthorize("(isClient() and hasAuthority('SCOPE_sgi-cnf')) or hasAuthority('ADM-CNF-E')")
  public ResponseEntity<Void> restoreDefaultValue(@PathVariable String name) {
    log.debug("restoreDefaultValue({}) - start", name);
    service.restoreDefaultValue(name);
    log.debug("restoreDefaultValue({}) - end", name);
    return new ResponseEntity<>(HttpStatus.OK);
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
    ResourceLastModifiedDateOutput returnValue = service.getLastModifiedDate(name);
    log.debug("getResourceLastModifiedDate({}) - end", name);

    return ResponseEntity.ok().lastModified(returnValue.getLastModifiedDate()).body(null);
  }

}
