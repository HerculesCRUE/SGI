package org.crue.hercules.sgi.cnf.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.cnf.exceptions.CnfNotFoundException;
import org.crue.hercules.sgi.cnf.model.Config;
import org.crue.hercules.sgi.cnf.repository.ConfigRepository;
import org.crue.hercules.sgi.cnf.util.AssertHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for {@link Config}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ConfigService {

  private final ConfigRepository repository;

  /**
   * The constructor
   * 
   * @param repository the {@link ConfigRepository} to access data
   */
  public ConfigService(ConfigRepository repository) {
    this.repository = repository;
  }

  /**
   * Creates a new {@link Config}.
   *
   * @param config the {@link Config} to create
   * @return the created {@link Config}
   */
  @Transactional
  @Validated({ Config.OnCreate.class })
  public Config create(@Valid Config config) {
    log.debug("create(Config config) - start");
    AssertHelper.fieldNotNull(config.getName(), Config.class, AssertHelper.MESSAGE_KEY_NAME);
    Config returnValue = repository.save(config);
    log.debug("create(Config config) - end");
    return returnValue;
  }

  /**
   * Updates an existing {@link Config}
   *
   * @param config the {@link Config} with modified data
   * @return the modified {@link Config}
   */
  @Transactional
  public Config update(Config config) {
    log.debug("update(Config config) - start");
    AssertHelper.fieldNotNull(config.getName(), Config.class, AssertHelper.MESSAGE_KEY_NAME);
    Config returnValue = repository.save(config);
    log.debug("update(Config config) - end");
    return returnValue;
  }

  /**
   * Updates the value of an existing {@link Config}
   * 
   * @param name  the name of the {@link Config} to update
   * @param value the new value
   * @return Config the updated {@link Config}
   */
  @Transactional
  public Config updateValue(String name, String value) {
    log.debug("updateValue(String name, String value) - start");

    AssertHelper.fieldNotNull(name, Config.class, AssertHelper.MESSAGE_KEY_NAME);

    Config config = repository.findById(name)
        .orElseThrow(() -> new CnfNotFoundException(name, Config.class));
    config.setValue(value);

    Config returnValue = repository.saveAndFlush(config);

    log.debug("updateValue(String name, String value) - end");
    return returnValue;
  }

  /**
   * Restore the default value of an existing {@link Config}
   * 
   * @param name the name of the {@link Config} to update
   * @return Config the updated {@link Config}
   */
  @Transactional
  public Config restoreDefaultValue(String name) {
    log.debug("restoreDefaultValue({}) - start", name);

    AssertHelper.fieldNotNull(name, Config.class, AssertHelper.MESSAGE_KEY_NAME);

    Config config = repository.findById(name)
        .orElseThrow(() -> new CnfNotFoundException(name, Config.class));
    config.setValue(config.getDefaultValue());

    Config returnValue = repository.saveAndFlush(config);

    log.debug("restoreDefaultValue({}) - end", name);
    return returnValue;
  }

  /**
   * Deletes an existing {@link Config}
   *
   * @param name the name of the {@link Config} to delete
   */
  @Transactional
  public void delete(String name) {
    log.debug("delete(String name) - start");
    AssertHelper.fieldNotNull(name, Config.class, AssertHelper.MESSAGE_KEY_NAME);
    repository.deleteById(name);
    log.debug("delete(String name) - end");
  }

  /**
   * Get a {@link Config}
   * 
   * @param name the name of the {@link Config} to get
   * @return the {@link Config} with the provided name
   */
  public Config get(String name) {
    log.debug("get(String name) - start");
    AssertHelper.fieldNotNull(name, Config.class, AssertHelper.MESSAGE_KEY_NAME);
    Config returnValue = repository.findById(name)
        .orElseThrow(() -> new CnfNotFoundException(name, Config.class));
    log.debug("get(String name) - end");
    return returnValue;
  }

  /**
   * Find {@link Config}
   *
   * @param pageable paging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link Config} pagged and filtered
   */
  public Page<Config> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Config> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Config> tasks = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return tasks;
  }

}