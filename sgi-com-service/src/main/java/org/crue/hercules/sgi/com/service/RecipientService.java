package org.crue.hercules.sgi.com.service;

import java.util.List;

import org.crue.hercules.sgi.com.model.Recipient;
import org.crue.hercules.sgi.com.repository.RecipientRepository;
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
public class RecipientService extends BaseService {
  private final RecipientRepository repository;

  public RecipientService(RecipientRepository repository) {
    log.debug(
        "RecipientService(RecipientRepository repository) - start");
    this.repository = repository;
    log.debug(
        "RecipientService(RecipientRepository repository) - end");
  }

  public List<Recipient> findByEmailId(Long id) {
    log.debug("findByEmailId(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Recipient.class))
            .build());
    List<Recipient> returnValue = repository.findByEmailId(id);
    log.debug("findByEmailId(Long id) - end");
    return returnValue;
  }
}
