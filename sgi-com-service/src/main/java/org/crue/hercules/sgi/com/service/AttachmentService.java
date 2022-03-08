package org.crue.hercules.sgi.com.service;

import java.util.List;

import org.crue.hercules.sgi.com.model.Attachment;
import org.crue.hercules.sgi.com.repository.AttachmentRepository;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Validated
@Slf4j
public class AttachmentService extends BaseService {

  private final AttachmentRepository repository;

  public AttachmentService(AttachmentRepository repository) {
    log.debug(
        "AttachmentService(AttachmentRepository repository) - start");
    this.repository = repository;
    log.debug(
        "AttachmentService(AttachmentRepository repository) - end");
  }

  public List<Attachment> findByEmailId(Long id) {
    log.debug("findByEmailId(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Attachment.class))
            .build());
    List<Attachment> returnValue = repository.findByEmailId(id);
    log.debug("findByEmailId(Long id) - end");
    return returnValue;
  }
}
