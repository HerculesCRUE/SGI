package org.crue.hercules.sgi.tp.controller;

import javax.validation.Valid;

import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.tp.converter.SgiApiTaskConverter;
import org.crue.hercules.sgi.tp.dto.SgiApiCronTaskInput;
import org.crue.hercules.sgi.tp.dto.SgiApiCronTaskOutput;
import org.crue.hercules.sgi.tp.dto.SgiApiInstantTaskInput;
import org.crue.hercules.sgi.tp.dto.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.tp.model.BeanMethodCronTask;
import org.crue.hercules.sgi.tp.model.BeanMethodInstantTask;
import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.crue.hercules.sgi.tp.service.BeanMethodTaskService;
import org.crue.hercules.sgi.tp.tasks.SgiApiCallerTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * SgiApiTaskController
 */
@RestController
@RequestMapping(SgiApiTaskController.MAPPING)
@Slf4j
public class SgiApiTaskController {
  public static final String PATH_SEPARATOR = "/";
  public static final String MAPPING = PATH_SEPARATOR + "sgiapitasks";
  public static final String PATH_CRON = PATH_SEPARATOR + "cron";
  public static final String PATH_INSTANT = PATH_SEPARATOR + "instant";
  public static final String PATH_SINGLE = PATH_SEPARATOR + "{id}";

  private BeanMethodTaskService beanMethodTaskService;

  public SgiApiTaskController(BeanMethodTaskService beanMethodTaskService) {
    this.beanMethodTaskService = beanMethodTaskService;
  }

  /**
   * Creates a new SGI API cron task.
   * 
   * @param cronTask the {@link SgiApiCronTaskInput} to be created
   * @return the newly created {@link SgiApiCronTaskOutput}
   */
  @PostMapping(PATH_CRON)
  public ResponseEntity<SgiApiCronTaskOutput> createSgiApiCronTask(@Valid @RequestBody SgiApiCronTaskInput cronTask) {
    log.debug("createSgiApiCronTask(SgiApiCronTaskInput cronTask) - start");
    BeanMethodCronTask returnValue = beanMethodTaskService.create(SgiApiTaskConverter.convert(cronTask));
    log.debug("createSgiApiCronTask(SgiApiCronTaskInput cronTask) - end");
    return new ResponseEntity<>(SgiApiTaskConverter.convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Creates a new SGI API instant task.
   * 
   * @param instantTask the {@link SgiApiInstantTaskInput} to be created
   * @return the newly created {@link SgiApiInstantTaskOutput}
   */
  @PostMapping(PATH_INSTANT)
  public ResponseEntity<SgiApiInstantTaskOutput> createSgiApiInstantTask(
      @Valid @RequestBody SgiApiInstantTaskInput instantTask) {
    log.debug("createSgiApiInstantTask(SgiApiInstantTaskInput instantTask) - start");
    BeanMethodInstantTask returnValue = beanMethodTaskService.create(SgiApiTaskConverter.convert(instantTask));
    log.debug("createSgiApiInstantTask(SgiApiInstantTaskInput instantTask) - end");
    return new ResponseEntity<>(SgiApiTaskConverter.convert(returnValue), HttpStatus.CREATED);
  }

  /**
   * Updates an existing SGI API cron task.
   * 
   * @param cronTask the new SGI API cron task data
   * @param id       identifier of the SGI API cron task to be updated
   * @return the {@link SgiApiCronTaskOutput} with the updated data
   */
  @PutMapping(PATH_CRON + PATH_SINGLE)
  public SgiApiCronTaskOutput updateSgiApiCronTask(@PathVariable Long id,
      @Valid @RequestBody SgiApiCronTaskInput cronTask) {
    log.debug("updateSgiApiCronTask(Long id, SgiApiCronTaskInput cronTask) - start");
    BeanMethodCronTask returnValue = beanMethodTaskService.update(SgiApiTaskConverter.convert(id, cronTask));
    log.debug("updateSgiApiCronTask(Long id, SgiApiCronTaskInput cronTask) - end");
    return SgiApiTaskConverter.convert(returnValue);
  }

  /**
   * Updates an existing SGI API instant task.
   * 
   * @param instantTask the new SGI API instant task data
   * @param id          identifier of the SGI API instant task to be updated
   * @return the {@link SgiApiInstantTaskOutput} with the updated data
   */
  @PutMapping(PATH_INSTANT + PATH_SINGLE)
  public SgiApiInstantTaskOutput updateSgiApiInstantTask(@PathVariable Long id,
      @Valid @RequestBody SgiApiInstantTaskInput instantTask) {
    log.debug("updateSgiApiInstantTask(Long id, SgiApiInstantTaskInput instantTask) - start");
    BeanMethodInstantTask returnValue = beanMethodTaskService.update(SgiApiTaskConverter.convert(id, instantTask));
    log.debug("updateSgiApiInstantTask(Long id, SgiApiInstantTaskInput instantTask) - end");
    return SgiApiTaskConverter.convert(returnValue);
  }

  /**
   * Delete existing {@link SgiApiCronTaskOutput} or
   * {@link SgiApiInstantTaskOutput}.
   * 
   * @param id the identifier
   */
  @DeleteMapping(PATH_SINGLE)
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    beanMethodTaskService.delete(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Returns a {@link SgiApiCronTaskOutput} or {@link SgiApiInstantTaskOutput}
   * with the provides identifier.
   * 
   * @param id the identifier
   * @return the element with the provided id
   */
  @GetMapping(PATH_SINGLE)
  public Object get(@PathVariable Long id) {
    log.debug("get(Long id) - start");
    BeanMethodTask value = beanMethodTaskService.get(id);
    if (SgiApiTaskConverter.BEAN_NAME_SGI_API_CALLER_TASK.equals(value.getBean())) {
      Object returnValue = SgiApiTaskConverter.convert(value);
      log.debug("get(Long id) - end");
      return returnValue;
    } else {
      throw new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
          .parameter("entity", ApplicationContextSupport.getMessage(SgiApiCallerTask.class)).parameter("id", id)
          .build());
    }
  }

  /**
   * Returns a paged and filtered list of enabled {@link SgiApiCronTaskOutput} and
   * {@link SgiApiInstantTaskOutput}.
   * 
   * @param query  search filter
   * @param paging page information
   * @return the paginated and filtered list
   */
  @GetMapping
  public ResponseEntity<Page<Object>> findEnabledSgiApiTasks(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findEnabledSgiApiTasks(String query, Pageable paging) - start");
    Page<BeanMethodTask> page = beanMethodTaskService.findEnabled(completeQuery(query), paging);

    ResponseEntity<Page<Object>> returnValue = null;
    if (page.isEmpty()) {
      returnValue = new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      returnValue = new ResponseEntity<>(SgiApiTaskConverter.convert(page), HttpStatus.OK);
    }
    log.debug("findEnabledSgiApiTasks(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Returns a paged and filtered list of enabled {@link SgiApiCronTaskOutput}.
   * 
   * @param query  search filter
   * @param paging page information
   * @return the paginated and filtered list
   */
  @GetMapping(PATH_CRON)
  public ResponseEntity<Page<SgiApiCronTaskOutput>> findEnabledSgiApiCronTasks(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findEnabledSgiApiCrontTasks(String query, Pageable paging) - start");
    Page<BeanMethodCronTask> page = beanMethodTaskService.findEnabledCronTasks(completeQuery(query), paging);

    ResponseEntity<Page<SgiApiCronTaskOutput>> returnValue = null;
    if (page.isEmpty()) {
      returnValue = new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } else {
      returnValue = new ResponseEntity<>(SgiApiTaskConverter.convertCron(page), HttpStatus.OK);
    }

    log.debug("findEnabledSgiApiCrontTasks(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Returns a paged and filtered list of enabled {@link SgiApiInstantTaskOutput}.
   * 
   * @param query  search filter
   * @param paging page information
   * @return the paginated and filtered list
   */
  @GetMapping(PATH_INSTANT)
  public ResponseEntity<Page<SgiApiInstantTaskOutput>> findEnabledSgiApiInstantTasks(
      @RequestParam(name = "q", required = false) String query, @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findEnabledSgiApiInstantTasks(String query, Pageable paging) - start");
    Page<BeanMethodInstantTask> page = beanMethodTaskService.findEnabledInstantTasks(completeQuery(query), paging);

    if (page.isEmpty()) {
      log.debug("findEnabledSgiApiInstantTasks(String query, Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    log.debug("findEnabledSgiApiInstantTasks(String query, Pageable paging) - end");
    return new ResponseEntity<>(SgiApiTaskConverter.convertInstant(page), HttpStatus.OK);
  }

  private String completeQuery(String query) {
    if (query == null) {
      return String.format("bean==%s", SgiApiTaskConverter.BEAN_NAME_SGI_API_CALLER_TASK);
    } else {
      return String.format("bean==%s;(%s)", SgiApiTaskConverter.BEAN_NAME_SGI_API_CALLER_TASK, query);
    }
  }

}