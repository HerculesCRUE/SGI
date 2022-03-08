package org.crue.hercules.sgi.tp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.tp.model.BeanMethodCronTask;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask;
import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.crue.hercules.sgi.tp.repository.BeanMethodTaskRepository;
import org.crue.hercules.sgi.tp.repository.specification.BeanMehtodTaskSpecifications;
import org.crue.hercules.sgi.tp.scheduling.BeanMethodTaskScheduler;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for {@link BeanMethodTask}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class BeanMethodTaskService {

  public static final String TASK_CACHE_KEY = "task";
  public static final String TASKS_CACHE_KEY = "tasks";
  public static final String CRON_TASKS_CACHE_KEY = "cron-tasks";
  public static final String INSTANT_TASKS_CACHE_KEY = "instant-tasks";
  public static final String ENABLED_TASKS_CACHE_KEY = "enabled-tasks";
  public static final String ENABLED_CRON_TASKS_CACHE_KEY = "enabled-cron-tasks";
  public static final String ENABLED_INSTANT_TASKS_CACHE_KEY = "enabled-instant-tasks";
  public static final String ENABLED_FUTURE_TASKS_CACHE_KEY = "enabled-future-tasks";

  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";
  private static final String PROBLEM_MESSAGE_ISNULL = "isNull";
  private static final String MESSAGE_KEY_ID = "id";

  private final BeanMethodTaskRepository repository;
  private final BeanMethodTaskScheduler scheduler;

  public BeanMethodTaskService(BeanMethodTaskRepository repository, BeanMethodTaskScheduler scheduler) {
    this.repository = repository;
    this.scheduler = scheduler;
  }

  /**
   * Creates and schedules a new {@link BeanMethodTask}
   *
   * @param <T>            the {@link BeanMethodTask} sub-type
   * @param beanMethodTask the {@link BeanMethodTask} to create
   * @return the created {@link BeanMethodTask}
   */

  @Caching(put = { @CachePut(value = TASK_CACHE_KEY, key = "#result.id", condition = "#result!=null") }, evict = {
      @CacheEvict(value = TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false") })

  @Transactional
  public <T extends BeanMethodTask> T create(T beanMethodTask) {
    log.debug("create(BeanMethodCronTask beanMethodTask) - start");
    Assert.isNull(beanMethodTask.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_ISNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .build());

    T returnValue = repository.save(beanMethodTask);
    scheduler.scheduleTask(returnValue);
    log.debug("create(BeanMethodCronTask beanMethodTask) - end");
    return returnValue;
  }

  /**
   * Updates and re-schedules a new {@link BeanMethodTask}
   *
   * @param <T>            the {@link BeanMethodTask} sub-type
   * @param beanMethodTask the {@link BeanMethodTask} to modify
   * @return the modified {@link BeanMethodTask}
   */

  @Caching(put = { @CachePut(value = TASK_CACHE_KEY, key = "#result.id", condition = "#result!=null") }, evict = {
      @CacheEvict(value = TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = CRON_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = INSTANT_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_CRON_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_INSTANT_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false") })

  @Transactional
  public <T extends BeanMethodTask> T update(T beanMethodTask) {
    log.debug("update(BeanMethodTask beanMethodTask) - start");
    Assert.notNull(beanMethodTask.getId(),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .build());

    T returnValue = repository.save(beanMethodTask);
    scheduler.scheduleTask(returnValue);
    log.debug("update(BeanMethodTask beanMethodTask) - end");
    return returnValue;
  }

  /**
   * Enables and schedules an existing {@link BeanMethodTask}
   *
   * @param id the identifier of the {@link BeanMethodTask} to enable
   * @return the enabled {@link BeanMethodTask}
   */

  @Caching(put = { @CachePut(value = TASK_CACHE_KEY, key = "#id", condition = "#result!=null") }, evict = {
      @CacheEvict(value = TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = CRON_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = INSTANT_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_CRON_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_INSTANT_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false") })

  @Transactional
  public BeanMethodTask enable(Long id) {
    log.debug("enable(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .build());
    BeanMethodTask task = repository.findById(id)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .parameter("id", id).build()));
    if (Boolean.TRUE.equals(task.getDisabled())) {
      task.setDisabled(Boolean.FALSE);
      BeanMethodTask returnValue = repository.save(task);
      scheduler.scheduleTask(returnValue);
      log.debug("enable(Long id) - end");
      return returnValue;
    }
    log.debug("enable(Long id) - end");
    return task;
  }

  /**
   * Disables and un-schedules an existing {@link BeanMethodTask}
   *
   * @param id the identifier of the {@link BeanMethodTask} to disable
   * @return the disabled {@link BeanMethodTask}
   */

  @Caching(put = { @CachePut(value = TASK_CACHE_KEY, key = "#result.id", condition = "#result!=null") }, evict = {
      @CacheEvict(value = TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = CRON_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = INSTANT_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_CRON_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_INSTANT_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false"),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true, condition = "#result!=null && #result.disabled==false") })

  @Transactional
  public BeanMethodTask disable(Long id) {
    log.debug("disable(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .build());
    BeanMethodTask task = repository.findById(id)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .parameter("id", id).build()));
    if (Boolean.FALSE.equals(task.getDisabled())) {
      task.setDisabled(Boolean.TRUE);
      BeanMethodTask returnValue = repository.save(task);
      scheduler.unScheduleTask(returnValue);
      log.debug("disable(Long id) - end");
      return returnValue;
    }
    log.debug("disable(Long id) - end");
    return task;
  }

  /**
   * Deletes and un-schedules a {@link BeanMethodTask}
   *
   * @param id the identifier of the {@link BeanMethodTask} to delete
   */
  @Caching(evict = { @CacheEvict(value = TASK_CACHE_KEY, key = "#id"),
      @CacheEvict(value = CRON_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = INSTANT_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_CRON_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_INSTANT_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true) })
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .build());
    repository.deleteById(id);
    scheduler.unScheduleTask(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Get a {@link BeanMethodTask}
   *
   * @param id the identifier of the {@link BeanMethodTask} to get
   * @return the {@link BeanMethodTask} with the provided id
   */
  @Cacheable(value = TASK_CACHE_KEY, key = "#id")
  public BeanMethodTask get(Long id) {
    log.debug("get(Long id) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .build());
    BeanMethodTask returnValue = repository.findById(id)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(BeanMethodTask.class))
            .parameter("id", id).build()));
    log.debug("get(Long id) - end");
    return returnValue;
  }

  /**
   * Find {@link BeanMethodTask}
   *
   * @param pageable paging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodTask} pagged and filtered
   */
  @Cacheable(TASKS_CACHE_KEY)
  public Page<BeanMethodTask> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return tasks;
  }

  /**
   * Find {@link BeanMethodCronTask}
   *
   * @param pageable paging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodCronTask} pagged and filtered
   */
  @Cacheable(CRON_TASKS_CACHE_KEY)
  @SuppressWarnings("unchecked")
  public Page<BeanMethodCronTask> findAllCronTasks(String query, Pageable pageable) {
    log.debug("findAllCronTasks(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> cron = BeanMehtodTaskSpecifications.isCronTasks();
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(cron.and(specs), pageable);
    List<BeanMethodCronTask> content = (List<BeanMethodCronTask>) (List<?>) tasks.getContent();
    Page<BeanMethodCronTask> cronTasks = new PageImpl<>(content, tasks.getPageable(), content.size());
    log.debug("findAllCronTasks(String query, Pageable pageable) - end");
    return cronTasks;
  }

  /**
   * Find {@link BeanMethodInstantTask}
   *
   * @param pageable paging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodInstantTask} pagged and filtered
   */
  @Cacheable(INSTANT_TASKS_CACHE_KEY)
  @SuppressWarnings("unchecked")
  public Page<BeanMethodInstantTask> findAllInstantTasks(String query, Pageable pageable) {
    log.debug("findAllInstantTasks(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> instant = BeanMehtodTaskSpecifications.isInstantTasks();
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(instant.and(specs), pageable);
    List<BeanMethodInstantTask> content = (List<BeanMethodInstantTask>) (List<?>) tasks.getContent();
    Page<BeanMethodInstantTask> instantTasks = new PageImpl<>(content, tasks.getPageable(), content.size());
    log.debug("findAllInstantTasks(String query, Pageable pageable) - end");
    return instantTasks;
  }

  /**
   * Find the enabled {@link BeanMethodTask}
   *
   * @param pageable paging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodTask} pagged and filtered
   */
  @Cacheable(ENABLED_TASKS_CACHE_KEY)
  public Page<BeanMethodTask> findEnabled(String query, Pageable pageable) {
    log.debug("findEnabled(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> enabled = BeanMehtodTaskSpecifications.enabled();
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(enabled.and(specs), pageable);
    log.debug("findEnabled(String query, Pageable pageable) - end");
    return tasks;
  }

  /**
   * Find the enabled {@link BeanMethodCronTask}
   *
   * @param pageable paging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodTask} pagged and filtered
   */
  @Cacheable(ENABLED_CRON_TASKS_CACHE_KEY)
  @SuppressWarnings("unchecked")
  public Page<BeanMethodCronTask> findEnabledCronTasks(String query, Pageable pageable) {
    log.debug("findEnabledCronTasks(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> cron = BeanMehtodTaskSpecifications.isCronTasks();
    Specification<BeanMethodTask> enabled = BeanMehtodTaskSpecifications.enabled();
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(cron.and(enabled).and(specs), pageable);
    List<BeanMethodCronTask> content = (List<BeanMethodCronTask>) (List<?>) tasks.getContent();
    Page<BeanMethodCronTask> cronTasks = new PageImpl<>(content, tasks.getPageable(), content.size());
    log.debug("findEnabledCronTasks(String query, Pageable pageable) - end");
    return cronTasks;
  }

  /**
   * Find the enabled {@link BeanMethodInstantTask}
   *
   * @param pageable paging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodInstantTask} pagged and filtered
   */
  @Cacheable(ENABLED_INSTANT_TASKS_CACHE_KEY)
  @SuppressWarnings("unchecked")
  public Page<BeanMethodInstantTask> findEnabledInstantTasks(String query, Pageable pageable) {
    log.debug("findEnabledInstantTasks(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> instant = BeanMehtodTaskSpecifications.isInstantTasks();
    Specification<BeanMethodTask> enabled = BeanMehtodTaskSpecifications.enabled();
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(instant.and(enabled).and(specs), pageable);
    List<BeanMethodInstantTask> content = (List<BeanMethodInstantTask>) (List<?>) tasks.getContent();
    Page<BeanMethodInstantTask> instantTasks = new PageImpl<>(content, tasks.getPageable(), content.size());
    log.debug("findEnabledInstantTasks(String query, Pageable pageable) - end");
    return instantTasks;
  }

  /**
   * Find the enabled {@link BeanMethodTask} including only the
   * {@link BeanMethodInstantTask} whose instant is after to current date-time.
   *
   * @param pageable paging info
   * @param query    RSQL expression with the restrictions to apply in the search
   * @return {@link BeanMethodTask} pagged and filtered
   */
  @Cacheable(ENABLED_FUTURE_TASKS_CACHE_KEY)
  public Page<BeanMethodTask> findEnabledIncludingBeanMethodInstantTaskIfFuture(String query, Pageable pageable) {
    log.debug("findEnabledButOnlyFuture(String query, Pageable pageable) - start");
    Specification<BeanMethodTask> future = BeanMehtodTaskSpecifications.includeBeanMethodInstantTaskIfFuture();
    Specification<BeanMethodTask> enabled = BeanMehtodTaskSpecifications.enabled();
    Specification<BeanMethodTask> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<BeanMethodTask> tasks = repository.findAll(enabled.and(future).and(specs), pageable);
    log.debug("findEnabledButOnlyFuture(String query, Pageable pageable) - end");
    return tasks;
  }

  /**
   * Disables all {@link BeanMethodInstantTask} whose instant is before to current
   * date-time.
   */
  @Caching(evict = { @CacheEvict(value = TASK_CACHE_KEY, allEntries = true),
      @CacheEvict(value = CRON_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = INSTANT_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_CRON_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_INSTANT_TASKS_CACHE_KEY, allEntries = true),
      @CacheEvict(value = ENABLED_FUTURE_TASKS_CACHE_KEY, allEntries = true) })
  @Transactional
  public void disablePast() {
    log.debug("disablePast() - start");
    log.info("Disabling Past BeanMethodTasks...");
    List<Long> ids = new ArrayList<>();
    List<BeanMethodTask> pastInstantTasks = repository.findAll(BeanMehtodTaskSpecifications.pastInstantTasks());
    for (BeanMethodTask task : pastInstantTasks) {
      task.setDisabled(Boolean.TRUE);
      update(task);
      ids.add(task.getId());
    }
    log.info("Disabled {} BeanMethodTasks. Ids: {}", ids.size(), Arrays.toString(ids.toArray()));
    log.debug("disablePast() - end");
  }

}