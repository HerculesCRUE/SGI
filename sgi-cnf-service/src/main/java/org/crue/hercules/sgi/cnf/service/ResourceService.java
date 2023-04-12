package org.crue.hercules.sgi.cnf.service;

import org.crue.hercules.sgi.cnf.dto.ResourceInfoOutput;
import org.crue.hercules.sgi.cnf.dto.ResourceLastModifiedDateOutput;
import org.crue.hercules.sgi.cnf.dto.ResourceValueOutput;
import org.crue.hercules.sgi.cnf.exceptions.CnfNotFoundException;
import org.crue.hercules.sgi.cnf.model.Resource;
import org.crue.hercules.sgi.cnf.repository.ResourceRepository;
import org.crue.hercules.sgi.cnf.util.AssertHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for {@link Resource}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
@RequiredArgsConstructor
public class ResourceService {

  private final ResourceRepository repository;

  /**
   * Updates the value of an existing {@link Resource}
   * 
   * @param name  the name of the {@link Resource} to update
   * @param value the new value
   */
  @Transactional
  public void updateValue(String name, byte[] value) {
    log.debug("updateValue({}, value) - start", name);

    AssertHelper.fieldNotNull(name, Resource.class, AssertHelper.MESSAGE_KEY_NAME);
    AssertHelper.fieldNotNull(value, Resource.class, AssertHelper.MESSAGE_KEY_VALUE);

    Resource resource = repository.findById(name).orElseThrow(() -> new CnfNotFoundException(name, Resource.class));
    resource.setValue(value);
    repository.save(resource);

    log.debug("updateValue({}, value) - end", name);
  }

  /**
   * Restore the default value of an existing {@link Resource}
   * 
   * @param name the name of the {@link Resource} to update
   */
  @Transactional
  public void restoreDefaultValue(String name) {
    log.debug("restoreDefaultValue({}) - start", name);

    AssertHelper.fieldNotNull(name, Resource.class, AssertHelper.MESSAGE_KEY_NAME);

    Resource resource = repository.findById(name).orElseThrow(() -> new CnfNotFoundException(name, Resource.class));
    resource.setValue(resource.getDefaultValue());
    repository.save(resource);

    log.debug("restoreDefaultValue({}) - end", name);
  }

  /**
   * Get {@link Resource} value
   * 
   * @param name the name of the {@link Resource} to get
   * @return the {@link Resource} with the provided name
   */
  public ResourceValueOutput getValue(String name) {
    log.debug("getValue({}) - start", name);
    ResourceValueOutput returnValue = getResource(name, ResourceValueOutput.class);
    log.debug("getValue({}) - end", name);
    return returnValue;
  }

  /**
   * Get {@link Resource} last modified date
   * 
   * @param name the name of the {@link Resource} to get
   * @return the {@link Resource} with the provided name
   */
  public ResourceLastModifiedDateOutput getLastModifiedDate(String name) {
    log.debug("getLastModifiedDate({}) - start", name);
    ResourceLastModifiedDateOutput returnValue = getResource(name, ResourceLastModifiedDateOutput.class);
    log.debug("getLastModifiedDate({}) - end", name);
    return returnValue;
  }

  /**
   * Get {@link Resource} value only if it has public access
   * 
   * @param name the name of the {@link Resource} to get
   * @return the {@link Resource} with the provided name
   */
  public ResourceValueOutput getPublicResourceValue(String name) {
    log.debug("getPublicResourceValue({}) - start", name);
    ResourceValueOutput returnValue = getPublicResource(name, ResourceValueOutput.class);
    log.debug("getPublicResourceValue({}) - end", name);
    return returnValue;
  }

  /**
   * Get {@link Resource} last modified date only if it has public access
   * 
   * @param name the name of the {@link Resource} to get
   * @return the {@link Resource} with the provided name
   */
  public ResourceLastModifiedDateOutput getPublicResourceLastModifiedDate(String name) {
    log.debug("getPublicResourceLastModifiedDate({}) - start", name);
    ResourceLastModifiedDateOutput returnValue = getPublicResource(name, ResourceLastModifiedDateOutput.class);
    log.debug("getPublicResourceLastModifiedDate({}) - end", name);
    return returnValue;
  }

  /**
   * Get {@link Resource} info
   * 
   * @param name the name of the {@link Resource} to get
   * @return the {@link Resource} with the provided name
   */
  public ResourceInfoOutput getInfo(String name) {
    log.debug("getInfo({}) - start", name);
    ResourceInfoOutput returnValue = getResource(name, ResourceInfoOutput.class);
    log.debug("getInfo({}) - end", name);
    return returnValue;
  }

  private <T> T getPublicResource(String name, Class<T> returnClass) {
    AssertHelper.fieldNotNull(name, Resource.class, AssertHelper.MESSAGE_KEY_NAME);
    return repository
        .findByNameAndPublicAccessTrue(name, returnClass)
        .orElseThrow(() -> new CnfNotFoundException(name, Resource.class));
  }

  private <T> T getResource(String name, Class<T> returnClass) {
    AssertHelper.fieldNotNull(name, Resource.class, AssertHelper.MESSAGE_KEY_NAME);
    return repository
        .findByName(name, returnClass)
        .orElseThrow(() -> new CnfNotFoundException(name, Resource.class));
  }

}
