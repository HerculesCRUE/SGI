package org.crue.hercules.sgi.com.service;

import java.util.List;

import org.crue.hercules.sgi.com.model.Param;
import org.crue.hercules.sgi.com.repository.ParamRepository;
import org.crue.hercules.sgi.com.repository.specification.ParamSpecifications;
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

@Service
@Transactional(readOnly = true)
@Validated
@Slf4j
public class ParamService extends BaseService {
  private final ParamRepository repository;

  public ParamService(ParamRepository repository) {
    log.debug(
        "EmailService(ParamRepository repository) - start");
    this.repository = repository;
    log.debug(
        "EmailService(ParamRepository repository) - end");
  }

  public List<Param> findSubjectTplParamByEmailTplName(String name) {
    log.debug("findSubjectTplParamByEmailTplName(String name) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    List<Param> returnValue = repository.findAll(ParamSpecifications.subjectTplParamByEmailTplName(name));
    log.debug("findSubjectTplParamByEmailTplName(String name) - end");
    return returnValue;
  }

  public List<Param> findSubjectTplParamByEmailTplName(String name, String query) {
    log.debug("findSubjectTplParamByEmailTplName(String name, String query) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    Specification<Param> specs = ParamSpecifications.subjectTplParamByEmailTplName(name)
        .and(SgiRSQLJPASupport.toSpecification(query));
    List<Param> returnValue = repository.findAll(specs);
    log.debug("findSubjectTplParamByEmailTplName(String name, String query) - end");
    return returnValue;
  }

  public Page<Param> findSubjectTplParamByEmailTplName(String name, String query, Pageable pageable) {
    log.debug("findSubjectTplParamByEmailTplName(String name, String query, Pageable pageable) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    Specification<Param> specs = ParamSpecifications.subjectTplParamByEmailTplName(name)
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<Param> returnValue = repository.findAll(specs, pageable);
    log.debug("findSubjectTplParamByEmailTplName(String name, String query, Pageable pageable) - end");
    return returnValue;
  }

  public List<Param> findContentTplParamByEmailTplName(String name) {
    log.debug("findContentTplParamByEmailTplName(String name) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    List<Param> returnValue = repository.findAll(ParamSpecifications.contentTplParamByEmailTplName(name));
    log.debug("findContentTplParamByEmailTplName(String name) - end");
    return returnValue;
  }

  public List<Param> findContentTplParamByEmailTplName(String name, String query) {
    log.debug("findContentTplParamByEmailTplName(String name, String query) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    Specification<Param> specs = ParamSpecifications.contentTplParamByEmailTplName(name)
        .and(SgiRSQLJPASupport.toSpecification(query));
    List<Param> returnValue = repository.findAll(specs);
    log.debug("findContentTplParamByEmailTplName(String name, String query) - end");
    return returnValue;
  }

  public Page<Param> findContentTplParamByEmailTplName(String name, String query, Pageable pageable) {
    log.debug("findContentTplParamByEmailTplName(String name, String query, Pageable pageable) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    Specification<Param> specs = ParamSpecifications.contentTplParamByEmailTplName(name)
        .and(SgiRSQLJPASupport.toSpecification(query));
    Page<Param> returnValue = repository.findAll(specs, pageable);
    log.debug("findContentTplParamByEmailTplName(String name, String query, Pageable pageable) - end");
    return returnValue;
  }

  public List<Param> findByEmailTplName(String name) {
    log.debug("findByEmailTplName(String name) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    List<Param> returnValue = repository.findAll(ParamSpecifications.byEmailTplName(name));
    log.debug("findByEmailTplName(String name) - end");
    return returnValue;
  }

  public List<Param> findByEmailTplName(String name, String query) {
    log.debug("findByEmailTplName(String name, String query) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    Specification<Param> specs = ParamSpecifications.byEmailTplName(name).and(SgiRSQLJPASupport.toSpecification(query));
    List<Param> returnValue = repository.findAll(specs);
    log.debug("findByEmailTplName(String name, String query) - end");
    return returnValue;
  }

  public Page<Param> findByEmailTplName(String name, String query, Pageable pageable) {
    log.debug("findByEmailTplName(String name, String query, Pageable pageable) - start");
    Assert.notNull(
        name,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_NAME))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Param.class))
            .build());
    Specification<Param> specs = ParamSpecifications.byEmailTplName(name).and(SgiRSQLJPASupport.toSpecification(query));
    Page<Param> returnValue = repository.findAll(specs, pageable);
    log.debug("findByEmailTplName(String name, String query, Pageable pageable) - start");
    return returnValue;
  }
}
