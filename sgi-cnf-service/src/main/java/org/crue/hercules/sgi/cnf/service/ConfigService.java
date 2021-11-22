package org.crue.hercules.sgi.cnf.service;

import javax.validation.Valid;

import org.crue.hercules.sgi.cnf.model.Config;
import org.crue.hercules.sgi.cnf.repository.ConfigRepository;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String MESSAGE_KEY_NAME = "name";
  private static final String MESSAGE_KEY_VALUE = "value";

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
    Assert.notNull(config.getName(),
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Config.class)).build());
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
    Assert.notNull(config.getName(),
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Config.class)).build());
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
    Assert.notNull(name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Config.class)).build());
    Assert.notNull(value,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_VALUE))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Config.class)).build());
    Config config = repository.findById(name)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Config.class))
            .parameter(MESSAGE_KEY_NAME, name).build()));
    config.setValue(value);
    Config returnValue = repository.saveAndFlush(config);
    log.debug("updateValue(String name, String value) - end");
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
    Assert.notNull(name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Config.class)).build());
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
    Assert.notNull(name,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Config.class)).build());
    Config returnValue = repository.findById(name)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Config.class))
            .parameter(MESSAGE_KEY_NAME, name).build()));
    log.debug("get(String name) - end");
    return returnValue;
  }

  /**
   * Find {@link Config}
   *
   * @param pageable pagging info
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