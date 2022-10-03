package org.crue.hercules.sgi.com.service;

import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.crue.hercules.sgi.com.enums.ErrorType;
import org.crue.hercules.sgi.com.exceptions.ContentException;
import org.crue.hercules.sgi.com.exceptions.ParamException;
import org.crue.hercules.sgi.com.exceptions.RecipientException;
import org.crue.hercules.sgi.com.exceptions.SubjectException;
import org.crue.hercules.sgi.com.model.Attachment;
import org.crue.hercules.sgi.com.model.Email;
import org.crue.hercules.sgi.com.model.Email.OnSend;
import org.crue.hercules.sgi.com.model.EmailAttachmentDeferrable;
import org.crue.hercules.sgi.com.model.EmailParam;
import org.crue.hercules.sgi.com.model.EmailParamDeferrable;
import org.crue.hercules.sgi.com.model.EmailRecipientDeferrable;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.model.Param;
import org.crue.hercules.sgi.com.model.Recipient;
import org.crue.hercules.sgi.com.model.Status;
import org.crue.hercules.sgi.com.repository.AttachmentRepository;
import org.crue.hercules.sgi.com.repository.EmailAttachmentDeferrableRepository;
import org.crue.hercules.sgi.com.repository.EmailParamDeferrableRepository;
import org.crue.hercules.sgi.com.repository.EmailParamRepository;
import org.crue.hercules.sgi.com.repository.EmailRecipientDeferrableRepository;
import org.crue.hercules.sgi.com.repository.EmailRepository;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.crue.hercules.sgi.com.repository.ParamRepository;
import org.crue.hercules.sgi.com.repository.RecipientRepository;
import org.crue.hercules.sgi.com.repository.StatusRepository;
import org.crue.hercules.sgi.com.service.EmailBuilderService.EmailData;
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
 * Service for {@link Email}.
 */
@Service
@Transactional(readOnly = true)
@Validated
@Slf4j
public class EmailService extends BaseService {
  private final Validator validator;
  private final EmailRepository repository;
  private final EmailTplRepository emailTplRepository;
  private final RecipientRepository recipientRepository;
  private final EmailParamRepository emailParamRepository;
  private final AttachmentRepository attachmentRepository;
  private final ParamRepository paramRepository;
  private final StatusRepository statusRepository;
  private final EmailBuilderService emailBuilderService;
  private final EmailSenderService emailSenderService;
  private final EmailRecipientDeferrableRepository emailRecipientDeferrableRepository;
  private final EmailAttachmentDeferrableRepository emailAttachmentDeferrableRepository;
  private final EmailParamDeferrableRepository emailParamDeferrableRepository;

  public EmailService(Validator validator,
      EmailRepository repository,
      EmailTplRepository emailTplRepository,
      RecipientRepository recipientRepository,
      EmailParamRepository emailParamRepository,
      AttachmentRepository attachmentRepository,
      ParamRepository paramRepository,
      StatusRepository statusRepository,
      EmailBuilderService emailBuilderService,
      EmailSenderService emailSenderService,
      EmailRecipientDeferrableRepository emailRecipientDeferrableRepository,
      EmailAttachmentDeferrableRepository emailAttachmentDeferrableRepository,
      EmailParamDeferrableRepository emailParamDeferrableRepository) {
    log.debug(
        "EmailService(Validator validator, EmailRepository repository, EmailTplRepository emailTplRepository, RecipientRepository recipientRepository, EmailParamRepository emailParamRepository, AttachmentRepository attachmentRepository, ParamRepository paramRepository, StatusRepository statusRepository, EmailBuilderService emailBuilderService, EmailSenderService emailSenderService) - start");
    this.validator = validator;
    this.repository = repository;
    this.emailTplRepository = emailTplRepository;
    this.recipientRepository = recipientRepository;
    this.emailParamRepository = emailParamRepository;
    this.paramRepository = paramRepository;
    this.attachmentRepository = attachmentRepository;
    this.statusRepository = statusRepository;
    this.emailBuilderService = emailBuilderService;
    this.emailSenderService = emailSenderService;
    this.emailRecipientDeferrableRepository = emailRecipientDeferrableRepository;
    this.emailAttachmentDeferrableRepository = emailAttachmentDeferrableRepository;
    this.emailParamDeferrableRepository = emailParamDeferrableRepository;
    log.debug(
        "EmailService(Validator validator, EmailRepository repository, EmailTplRepository emailTplRepository, RecipientRepository recipientRepository, EmailParamRepository emailParamRepository, AttachmentRepository attachmentRepository, ParamRepository paramRepository, StatusRepository statusRepository, EmailBuilderService emailBuilderService, EmailSenderService emailSenderService) - end");
  }

  /**
   * Creates a new {@link Email}.
   *
   * @param email       the {@link Email} to create
   * @param recipients  the recipient list
   * @param attachments the attachment list
   * @param emailParams the param list
   * @return the new {@link Email}
   */
  @Transactional
  @Validated({ Email.OnCreate.class })
  public Email create(@Valid Email email, List<Recipient> recipients, List<Attachment> attachments,
      List<EmailParam> emailParams) {
    log.debug(
        "create(@Valid Email email, List<Recipient> recipients, List<Attachment> attachments, List<EmailParam> emailParams) - start");
    Assert.isNull(email.getId(),
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Email.class)).build());
    if (CollectionUtils.isNotEmpty(recipients)) {
      // Make sure all recipient emailId are null
      recipients.stream().forEach(r -> Assert.isNull(r.getEmailId(),
          () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NULL)
              .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_EMAIL_ID))
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Recipient.class))
              .build()));
    }
    if (CollectionUtils.isNotEmpty(attachments)) {
      // Make sure all attachment emailId are null
      attachments.stream().forEach(a -> Assert.isNull(a.getEmailId(),
          () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NULL)
              .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_EMAIL_ID))
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Attachment.class))
              .build()));
    }
    if (CollectionUtils.isNotEmpty(emailParams)) {
      emailParams.stream().forEach(e ->
      // Make sure all emailParam emailId are null
      Assert.isNull(e.getPk().getEmailId(),
          () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NULL)
              .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_EMAIL_ID))
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(EmailParam.class))
              .build()));
    }

    // Fetch EmailTpl from database
    EmailTpl emailTpl = emailTplRepository.findByName(email.getEmailTpl().getName())
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                EmailTpl.class))
            .parameter(MESSAGE_KEY_ID, email.getEmailTpl().getName()).build()));
    email.setEmailTpl(emailTpl);

    Email returnValue = repository.save(email);

    // Set id and save mapped entities
    EmailRecipientDeferrable emailRecipientDeferrable = email.getDeferrableRecipients();
    if (ObjectUtils.isNotEmpty(emailRecipientDeferrable)) {
      emailRecipientDeferrable.setId(returnValue.getId());
      returnValue.setDeferrableRecipients(emailRecipientDeferrableRepository.save(emailRecipientDeferrable));
    }
    EmailAttachmentDeferrable emailAttachmentDeferrable = email.getDeferrableAttachments();
    if (ObjectUtils.isNotEmpty(emailAttachmentDeferrable)) {
      emailAttachmentDeferrable.setId(returnValue.getId());
      returnValue.setDeferrableAttachments(emailAttachmentDeferrableRepository.save(emailAttachmentDeferrable));
    }
    EmailParamDeferrable emailParamDeferrable = email.getDeferrableParams();
    if (ObjectUtils.isNotEmpty(emailParamDeferrable)) {
      emailParamDeferrable.setId(returnValue.getId());
      returnValue.setDeferrableParams(emailParamDeferrableRepository.save(emailParamDeferrable));
    }
    // Set emailId and save related entities
    if (CollectionUtils.isNotEmpty(recipients)) {
      recipients.stream().forEach(r -> r.setEmailId(returnValue.getId()));
      recipientRepository.saveAll(recipients);
    }
    if (CollectionUtils.isNotEmpty(attachments)) {
      attachments.stream().forEach(r -> r.setEmailId(returnValue.getId()));
      attachmentRepository.saveAll(attachments);
    }
    if (CollectionUtils.isNotEmpty(emailParams)) {
      emailParams.stream().forEach(e -> {
        e.getPk().setEmailId(returnValue.getId());
        // Fetch Param from database
        Param param = paramRepository.findByName(e.getParam().getName())
            .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
                .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                    Param.class))
                .parameter(MESSAGE_KEY_ID, e.getParam().getName()).build()));
        e.setParam(param);
      });
      emailParamRepository.saveAll(emailParams);
    }

    log.debug(
        "create(@Valid Email email, List<Recipient> recipients, List<Attachment> attachments, List<EmailParam> emailParams) - end");
    return returnValue;
  }

  /**
   * Updates an existing {@link Email}
   *
   * @param email       the {@link Email} with modified data
   * @param recipients  the recipient list
   * @param attachments the attachment list
   * @param emailParams the param list
   * @return the modified {@link Email}
   */
  @Transactional
  public Email update(Email email, List<Recipient> recipients, List<Attachment> attachments,
      List<EmailParam> emailParams) {
    log.debug(
        "update(Email email, List<Recipient> recipients, List<Attachment> attachments, List<EmailParam> emailParams) - start");
    Assert.notNull(email.getId(),
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Email.class)).build());

    if (CollectionUtils.isNotEmpty(recipients)) {
      // Make sure all recipient emailId are correct
      Assert.isTrue(
          recipients.stream()
              .allMatch(r -> r.getEmailId().equals(email.getId())),
          // Defer message resolution untill is needed
          () -> ProblemMessage.builder().key(PROBLEM_MESSAGE_NORELATEDENTYTY)
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Recipient.class))
              .parameter(PROBLEM_MESSAGE_PARAMETER_RELATED, ApplicationContextSupport.getMessage(Email.class)).build());
    }
    if (CollectionUtils.isNotEmpty(attachments)) {
      // Make sure all attachment emailId are correct
      Assert.isTrue(
          recipients.stream()
              .allMatch(a -> a.getEmailId().equals(email.getId())),
          // Defer message resolution untill is needed
          () -> ProblemMessage.builder().key(PROBLEM_MESSAGE_NORELATEDENTYTY)
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Attachment.class))
              .parameter(PROBLEM_MESSAGE_PARAMETER_RELATED, ApplicationContextSupport.getMessage(Email.class)).build());
    }
    if (CollectionUtils.isNotEmpty(emailParams)) {
      emailParams.stream().forEach(e -> {
        Assert.isTrue(e.getPk().getEmailId().equals(email.getId()),
            // Defer message resolution untill is needed
            () -> ProblemMessage.builder().key(PROBLEM_MESSAGE_NORELATEDENTYTY)
                .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(EmailParam.class))
                .parameter(PROBLEM_MESSAGE_PARAMETER_RELATED, ApplicationContextSupport.getMessage(Email.class))
                .build());
        // Fetch Param from database
        Param param = paramRepository.findByName(e.getParam().getName())
            .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
                .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                    Param.class))
                .parameter(MESSAGE_KEY_ID, e.getParam().getName()).build()));
        e.setParam(param);
      });
    }
    if (ObjectUtils.isNotEmpty(email.getDeferrableAttachments())) {
      Assert.isTrue(email.getDeferrableAttachments().getId().equals(email.getId()),
          // Defer message resolution untill is needed
          () -> ProblemMessage.builder().key(PROBLEM_MESSAGE_NORELATEDENTYTY)
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                  ApplicationContextSupport.getMessage(EmailAttachmentDeferrable.class))
              .parameter(PROBLEM_MESSAGE_PARAMETER_RELATED, ApplicationContextSupport.getMessage(Email.class))
              .build());
    }
    if (ObjectUtils.isNotEmpty(email.getDeferrableParams())) {
      Assert.isTrue(email.getDeferrableParams().getId().equals(email.getId()),
          // Defer message resolution untill is needed
          () -> ProblemMessage.builder().key(PROBLEM_MESSAGE_NORELATEDENTYTY)
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                  ApplicationContextSupport.getMessage(EmailParamDeferrable.class))
              .parameter(PROBLEM_MESSAGE_PARAMETER_RELATED, ApplicationContextSupport.getMessage(Email.class))
              .build());
    }
    if (ObjectUtils.isNotEmpty(email.getDeferrableRecipients())) {
      Assert.isTrue(email.getDeferrableRecipients().getId().equals(email.getId()),
          // Defer message resolution untill is needed
          () -> ProblemMessage.builder().key(PROBLEM_MESSAGE_NORELATEDENTYTY)
              .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                  ApplicationContextSupport.getMessage(EmailRecipientDeferrable.class))
              .parameter(PROBLEM_MESSAGE_PARAMETER_RELATED, ApplicationContextSupport.getMessage(Email.class))
              .build());
    }

    // Delete existing mapped entities
    emailRecipientDeferrableRepository.deleteAllById(email.getId());
    emailAttachmentDeferrableRepository.deleteAllById(email.getId());
    emailAttachmentDeferrableRepository.deleteAllById(email.getId());
    // Delete existing related entities
    recipientRepository.deleteByEmailId(email.getId());
    attachmentRepository.deleteByEmailId(email.getId());
    emailParamRepository.deleteByPkEmailId(email.getId());

    // Fetch EmailTpl from database
    EmailTpl emailTpl = emailTplRepository.findByName(email.getEmailTpl().getName())
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                EmailTpl.class))
            .parameter(MESSAGE_KEY_ID, email.getEmailTpl().getName()).build()));
    email.setEmailTpl(emailTpl);

    Email returnValue = repository.save(email);

    // Set id and save mapped entities
    EmailRecipientDeferrable emailRecipientDeferrable = email.getDeferrableRecipients();
    if (ObjectUtils.isNotEmpty(emailRecipientDeferrable)) {
      returnValue.setDeferrableRecipients(emailRecipientDeferrableRepository.save(emailRecipientDeferrable));
    }
    EmailAttachmentDeferrable emailAttachmentDeferrable = email.getDeferrableAttachments();
    if (ObjectUtils.isNotEmpty(emailAttachmentDeferrable)) {
      returnValue.setDeferrableAttachments(emailAttachmentDeferrableRepository.save(emailAttachmentDeferrable));
    }
    EmailParamDeferrable emailParamDeferrable = email.getDeferrableParams();
    if (ObjectUtils.isNotEmpty(emailParamDeferrable)) {
      returnValue.setDeferrableParams(emailParamDeferrableRepository.save(emailParamDeferrable));
    }
    // Save related entities
    if (CollectionUtils.isNotEmpty(recipients)) {
      recipientRepository.saveAll(recipients);
    }
    if (CollectionUtils.isNotEmpty(attachments)) {
      attachmentRepository.saveAll(attachments);
    }
    if (CollectionUtils.isNotEmpty(emailParams)) {
      emailParamRepository.saveAll(emailParams);
    }
    log.debug(
        "update(Email email, List<Recipient> recipients, List<Attachment> attachments, List<EmailParam> emailParams) - end");
    return returnValue;
  }

  /**
   * Deletes an existing {@link Email}
   *
   * @param id the identifier of the {@link Email} to delete
   */
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Email.class)).build());
    // Delete mapped entities
    emailRecipientDeferrableRepository.deleteAllById(id);
    emailAttachmentDeferrableRepository.deleteAllById(id);
    emailAttachmentDeferrableRepository.deleteAllById(id);
    // Delete related entities
    recipientRepository.deleteByEmailId(id);
    attachmentRepository.deleteByEmailId(id);
    emailParamRepository.deleteByPkEmailId(id);
    statusRepository.deleteByEmailId(id);

    // Delete entity
    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Get an {@link Email}
   * 
   * @param id the id of the {@link Email} to get
   * @return the {@link Email} with the provided id
   */
  public Email get(Long id) {
    log.debug("get(String name) - start");
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Email.class)).build());
    Email returnValue = repository.findById(id)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Email.class))
            .parameter(MESSAGE_KEY_ID, id).build()));
    log.debug("get(Long id) - end");
    return returnValue;
  }

  /**
   * Find {@link Email}
   *
   * @param query RSQL expression with the restrictions to apply in the search
   * @return filtered {@link Email} list
   */
  public List<Email> findAll(String query) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Email> specs = SgiRSQLJPASupport.toSpecification(query);

    List<Email> tasks = repository.findAll(specs);
    log.debug("findAll(String query, Pageable pageable) - end");
    return tasks;
  }

  /**
   * Find {@link Email}
   *
   * @param query    RSQL expression with the restrictions to apply in the search
   * @param pageable paging info
   * @return {@link Email} pagged and filtered
   */
  public Page<Email> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<Email> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Email> tasks = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return tasks;
  }

  /**
   * Sends the {@link Email} with the given id.
   * 
   * @param id {@link Email} identifier.
   * @return the generated sending {@link Status}
   */
  @Transactional
  public Status send(Long id) {
    log.debug("send(Long id) - start");

    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class,
            PROBLEM_MESSAGE_NOTNULL)
            .parameter(
                PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(
                    MESSAGE_KEY_ID))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Email.class)).build());

    Email email = repository.findById(id)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Email.class))
            .parameter(MESSAGE_KEY_ID, id).build()));

    // OnSend validations
    Set<ConstraintViolation<Email>> result = validator.validate(email, OnSend.class);
    if (!result.isEmpty()) {
      throw new ConstraintViolationException(result);
    }

    EmailData emailData = null;
    Status status = null;
    try {
      emailData = emailBuilderService.build(email);
      emailSenderService.sendMessage(emailData.getRecipients(), emailData.getSubject(), emailData.getTextBody(),
          emailData.getHtmlBody(), emailData.getAttachments());
      status = Status.builder().emailId(id).message("SENT").build();
    } catch (RecipientException e) {
      log.error(e.getMessage(), e);
      status = Status.builder().emailId(id).error(ErrorType.RECIPIENT).message(ExceptionUtils.getStackTrace(e)).build();
    } catch (SubjectException e) {
      log.error(e.getMessage(), e);
      status = Status.builder().emailId(id).error(ErrorType.SUBJECT).message(ExceptionUtils.getStackTrace(e)).build();
    } catch (ContentException e) {
      log.error(e.getMessage(), e);
      status = Status.builder().emailId(id).error(ErrorType.CONTENT).message(ExceptionUtils.getStackTrace(e)).build();
    } catch (ParamException e) {
      log.error(e.getMessage(), e);
      status = Status.builder().emailId(id).error(ErrorType.PARAM).message(ExceptionUtils.getStackTrace(e)).build();
    } catch (MessagingException e) {
      log.error(e.getMessage(), e);
      status = Status.builder().emailId(id).error(ErrorType.SEND).message(ExceptionUtils.getStackTrace(e)).build();
    }
    if (status != null) {
      status = statusRepository.save(status);
    }
    log.debug("send(Long id) - end");
    return status;
  }

}
