package org.crue.hercules.sgi.com.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.activation.DataSource;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.com.dto.ProcessedEmailTpl;
import org.crue.hercules.sgi.com.exceptions.ContentException;
import org.crue.hercules.sgi.com.exceptions.ParamException;
import org.crue.hercules.sgi.com.exceptions.RecipientException;
import org.crue.hercules.sgi.com.exceptions.SubjectException;
import org.crue.hercules.sgi.com.exceptions.UnknownParamTypeException;
import org.crue.hercules.sgi.com.freemarker.FreemarkerEmailTemplateProcessor;
import org.crue.hercules.sgi.com.model.Attachment;
import org.crue.hercules.sgi.com.model.Email;
import org.crue.hercules.sgi.com.model.EmailAttachmentDeferrable;
import org.crue.hercules.sgi.com.model.EmailParam;
import org.crue.hercules.sgi.com.model.EmailParamDeferrable;
import org.crue.hercules.sgi.com.model.EmailRecipientDeferrable;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.model.Param;
import org.crue.hercules.sgi.com.model.Recipient;
import org.crue.hercules.sgi.com.repository.AttachmentRepository;
import org.crue.hercules.sgi.com.repository.EmailParamRepository;
import org.crue.hercules.sgi.com.repository.RecipientRepository;
import org.crue.hercules.sgi.com.service.sgi.SgiApiDocumentRefsService;
import org.crue.hercules.sgi.com.service.sgi.SgiApiEmailParamsService;
import org.crue.hercules.sgi.com.service.sgi.SgiApiInternetAddressesService;
import org.crue.hercules.sgi.com.service.sgi.SgiApiSgdocService;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@Validated
@Slf4j
public class EmailBuilderService extends BaseService {
  private final RecipientRepository recipientRepository;
  private final AttachmentRepository attachmentRepository;
  private final EmailParamRepository emailParamRepository;
  private final SgiApiInternetAddressesService sgiApiInternetAddressesService;
  private final SgiApiDocumentRefsService sgiApiDocumentRefsService;
  private final SgiApiEmailParamsService sgiApiEmailParamsService;
  private final SgiApiSgdocService sgiApiSgdocService;

  private final FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor;

  public EmailBuilderService(
      FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor,
      RecipientRepository recipientRepository,
      AttachmentRepository attachmentRepository,
      EmailParamRepository emailParamRepository,
      SgiApiInternetAddressesService sgiApiInternetAddressesService,
      SgiApiDocumentRefsService sgiApiDocumentRefsService,
      SgiApiEmailParamsService sgiApiEmailParamsService,
      SgiApiSgdocService sgiApiSgdocService) {
    log.debug(
        "EmailBuilderService(FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor, RecipientRepository recipientRepository, AttachmentRepository attachmentRepository, EmailParamRepository emailParamRepository, SgiApiInternetAddressesService sgiApiInternetAddressesService, SgiApiDocumentRefsService sgiApiDocumentRefsService, SgiApiEmailParamsService sgiApiEmailParamsService, SgiApiSgdocService sgiApiSgdocService) - start");
    this.freemarkerEmailTemplateProcessor = freemarkerEmailTemplateProcessor;
    this.recipientRepository = recipientRepository;
    this.attachmentRepository = attachmentRepository;
    this.emailParamRepository = emailParamRepository;
    this.sgiApiInternetAddressesService = sgiApiInternetAddressesService;
    this.sgiApiDocumentRefsService = sgiApiDocumentRefsService;
    this.sgiApiEmailParamsService = sgiApiEmailParamsService;
    this.sgiApiSgdocService = sgiApiSgdocService;
    log.debug(
        "EmailBuilderService(FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor, RecipientRepository recipientRepository, AttachmentRepository attachmentRepository, EmailParamRepository emailParamRepository, SgiApiInternetAddressesService sgiApiInternetAddressesService, SgiApiDocumentRefsService sgiApiDocumentRefsService, SgiApiEmailParamsService sgiApiEmailParamsService, SgiApiSgdocService sgiApiSgdocService) - end");
  }

  public EmailData build(Email email) throws ContentException, RecipientException, ParamException, SubjectException {
    log.debug("build(Email email) - start");
    Assert.notNull(
        email,
        () -> ProblemMessage.builder().key(Assert.class,
            PROBLEM_MESSAGE_NOTNULL)
            .parameter(
                PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(
                    MESSAGE_KEY_EMAIL))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(Email.class)).build());
    EmailTpl template = email.getEmailTpl();
    Assert.notNull(
        template,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage(
                MESSAGE_KEY_EMAIL_TPL))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(
                Email.class))
            .build());

    List<InternetAddress> internetAddresses = getRecipients(email);
    List<DataSource> attachments = getAttachments(email);
    HashMap<String, Object> params = getParams(email);

    ProcessedEmailTpl result = freemarkerEmailTemplateProcessor.processTemplate(template.getName(), params);

    EmailData returnValue = EmailData.builder().subject(
        result.getSubject()).textBody(
            result.getContentText())
        .htmlBody(result.getContentHtml())
        .recipients(internetAddresses)
        .attachments(attachments).build();
    log.debug("build(Email email) - end");
    return returnValue;
  }

  private List<InternetAddress> getRecipients(Email email) throws RecipientException {
    List<Recipient> recipients = recipientRepository.findByEmailId(email.getId());
    List<InternetAddress> addresses = new ArrayList<>();
    for (Recipient recipient : recipients) {
      addresses.add(convertRecipient(recipient));
    }

    EmailRecipientDeferrable emailRecipientDeferrable = email.getDeferrableRecipients();
    if (ObjectUtils.isNotEmpty(emailRecipientDeferrable)) {
      List<org.crue.hercules.sgi.com.dto.Recipient> deferredAddresses = null;
      try {
        deferredAddresses = sgiApiInternetAddressesService.call(emailRecipientDeferrable.getType(),
            emailRecipientDeferrable.getUrl(),
            emailRecipientDeferrable.getMethod());
      } catch (Exception e) {
        throw new RecipientException(e);
      }
      if (CollectionUtils.isNotEmpty(deferredAddresses)) {
        for (org.crue.hercules.sgi.com.dto.Recipient recipient : deferredAddresses) {
          addresses.add(convertRecipient(recipient));
        }
      }
    }

    return addresses;
  }

  private List<DataSource> getAttachments(Email email) throws ParamException {
    List<String> attachments = attachmentRepository.findByEmailId(email.getId()).stream()
        .map(Attachment::getDocumentRef).collect(Collectors.toList());

    EmailAttachmentDeferrable emailAttachmentDeferrable = email.getDeferrableAttachments();
    if (ObjectUtils.isNotEmpty(emailAttachmentDeferrable)) {
      List<String> deferredAttachments = null;
      try {
        deferredAttachments = sgiApiDocumentRefsService.call(emailAttachmentDeferrable.getType(),
            emailAttachmentDeferrable.getUrl(),
            emailAttachmentDeferrable.getMethod());
      } catch (Exception e) {
        throw new ParamException(e);
      }
      attachments.addAll(deferredAttachments);
    }

    List<DataSource> returnValue = new ArrayList<>();
    for (String documentRef : attachments) {
      returnValue.add(getDataSource(documentRef));
    }

    return returnValue;
  }

  private DataSource getDataSource(String documentRef) {
    return sgiApiSgdocService.call(documentRef);
  }

  private HashMap<String, Object> getParams(Email email) throws ParamException {
    List<EmailParam> params = emailParamRepository.findByPkEmailId(email.getId());

    EmailParamDeferrable emailParamDeferrable = email.getDeferrableParams();
    if (ObjectUtils.isNotEmpty(emailParamDeferrable)) {
      List<org.crue.hercules.sgi.com.dto.EmailParam> deferredParams = null;
      try {
        deferredParams = sgiApiEmailParamsService.call(emailParamDeferrable.getType(),
            emailParamDeferrable.getUrl(),
            emailParamDeferrable.getMethod());
      } catch (Exception e) {
        throw new ParamException(e);
      }
      List<EmailParam> defferedEmailParams = convertParams(email.getEmailTpl(), deferredParams);
      params.addAll(defferedEmailParams);
    }

    HashMap<String, Object> returnValue = new HashMap<>();
    for (EmailParam param : params) {
      returnValue.put(param.getParam().getName(), getEmailParamValue(param));
    }

    // TODO check all template params are resolved
    return returnValue;
  }

  private InternetAddress convertRecipient(Recipient recipient) throws RecipientException {
    if (recipient.getName() != null) {
      try {
        return new InternetAddress(recipient.getAddress(), recipient.getName());
      } catch (UnsupportedEncodingException e) {
        log.error(e.getMessage(), e);
      }
    }
    try {
      return new InternetAddress(recipient.getAddress());
    } catch (AddressException e) {
      throw new RecipientException(e);
    }
  }

  private InternetAddress convertRecipient(org.crue.hercules.sgi.com.dto.Recipient recipient)
      throws RecipientException {
    if (recipient.getName() != null) {
      try {
        return new InternetAddress(recipient.getAddress(), recipient.getName());
      } catch (UnsupportedEncodingException e) {
        log.error(e.getMessage(), e);
      }
    }
    try {
      return new InternetAddress(recipient.getAddress());
    } catch (AddressException e) {
      throw new RecipientException(e);
    }
  }

  private Object getEmailParamValue(EmailParam param) {
    Object value = null;
    switch (param.getParam().getType()) {
      case STRING:
        value = param.getValue();
        break;
      case JSON:
        // TODO check json correctness
        value = param.getValue();
        break;
      default:
        throw new UnknownParamTypeException(param.getParam().getType().name());
    }
    return value;
  }

  private List<EmailParam> convertParams(EmailTpl template,
      List<org.crue.hercules.sgi.com.dto.EmailParam> params) {
    List<EmailParam> paramEntities = new ArrayList<>();
    List<Param> templateParams = template.getSubjectTpl().getParams();
    templateParams.addAll(template.getContentTpl().getParams());
    HashMap<String, Param> templateParamsMap = new HashMap<>();
    for (Param param : templateParams) {
      templateParamsMap.put(param.getName(), param);
    }

    // TODO check if the templateParams contains the param name
    paramEntities.addAll(params.stream()
        .map(
            param -> EmailParam.builder().param(
                Param.builder().name(param.getName()).build()).value(param.getValue()).build())
        .collect(Collectors.toList()));

    return paramEntities;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class EmailData {
    private String subject;
    private String textBody;
    private String htmlBody;
    private List<InternetAddress> recipients;
    private List<DataSource> attachments;
  }

}
