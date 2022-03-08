package org.crue.hercules.sgi.com.converter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.com.dto.EmailInput;
import org.crue.hercules.sgi.com.dto.EmailOutput;
import org.crue.hercules.sgi.com.dto.EmailParam;
import org.crue.hercules.sgi.com.dto.Param;
import org.crue.hercules.sgi.com.dto.Recipient;
import org.crue.hercules.sgi.com.dto.Status;
import org.crue.hercules.sgi.com.model.Attachment;
import org.crue.hercules.sgi.com.model.Email;
import org.crue.hercules.sgi.com.model.EmailAttachmentDeferrable;
import org.crue.hercules.sgi.com.model.EmailParamDeferrable;
import org.crue.hercules.sgi.com.model.EmailParamPK;
import org.crue.hercules.sgi.com.model.EmailRecipientDeferrable;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for DTO from/to Entity conversion.
 */
@Slf4j
public class TypeConverter {
  private static ModelMapper modelMapper = new ModelMapper();
  static {
    // Email --> EmailOutput
    TypeMap<Email, EmailOutput> emailPropertyMapper = modelMapper.createTypeMap(Email.class, EmailOutput.class);
    emailPropertyMapper.addMapping(src -> {
      if (src.getEmailTpl().getName() != null) {
        return src.getEmailTpl().getName();
      } else {
        return null;
      }
    }, EmailOutput::setTemplate);

    // EmailInput --> Email
    TypeMap<EmailInput, Email> reverseEmailPropertyMapper = modelMapper.createTypeMap(EmailInput.class, Email.class);
    reverseEmailPropertyMapper.addMapping(EmailInput::getTemplate,
        (dest,
            v) -> {
          dest.getEmailTpl().setName((String) v);
        });

    // EmailParam (Entity) --> EmailParam (DTO)
    TypeMap<org.crue.hercules.sgi.com.model.EmailParam, EmailParam> emailParamPropertyMapper = modelMapper
        .createTypeMap(
            org.crue.hercules.sgi.com.model.EmailParam.class, EmailParam.class);
    emailParamPropertyMapper.addMapping(src -> {
      if (src.getParam().getName() != null) {
        return src.getParam().getName();
      } else {
        return null;
      }
    }, EmailParam::setName);

    // EmailParam (DTO) --> EmailParam (Entity)
    TypeMap<EmailParam, org.crue.hercules.sgi.com.model.EmailParam> reverseEmailParamPropertyMapper = modelMapper
        .createTypeMap(EmailParam.class,
            org.crue.hercules.sgi.com.model.EmailParam.class);
    reverseEmailParamPropertyMapper.addMapping(EmailParam::getName,
        (dest,
            v) -> {
          dest.getParam().setName((String) v);
        });
  }

  /**
   * Utility class, can't be instantiated.
   */
  private TypeConverter() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Converts from {@link EmailInput} to {@link Email}.
   * 
   * @param input the {@link EmailInput} to convert
   * @return the converted {@link Email}
   */
  public static Email convert(EmailInput input) {
    log.debug("convert(EmailInput input) - start");
    Email returnValue = convert(null, input);
    log.debug("convert(EmailInput input) - end");
    return returnValue;
  }

  /**
   * Converts from {@link EmailInput} to {@link Email}.
   * 
   * @param id    the {@link Email} id to set
   * @param input the {@link EmailInput} to be converted
   * @return the converted {@link Email}
   */
  public static Email convert(Long id, EmailInput input) {
    log.debug("convert(Long id, EmailInput input) - start");
    Email returnValue = modelMapper.map(input, Email.class);
    returnValue.setId(id);
    EmailAttachmentDeferrable emailAttachmentDeferrable = returnValue.getDeferrableAttachments();
    if (ObjectUtils.isNotEmpty(emailAttachmentDeferrable)) {
      emailAttachmentDeferrable.setId(id);
    }
    EmailRecipientDeferrable emailRecipientDeferrable = returnValue.getDeferrableRecipients();
    if (ObjectUtils.isNotEmpty(emailRecipientDeferrable)) {
      emailRecipientDeferrable.setId(id);
    }
    EmailParamDeferrable emailParamDeferrable = returnValue.getDeferrableParams();
    if (ObjectUtils.isNotEmpty(emailParamDeferrable)) {
      emailParamDeferrable.setId(id);
    }
    log.debug("convert(Long id, EmailInput input) - end");
    return returnValue;
  }

  /**
   * Converts from {@link Email} to {@link EmailOutput}.
   * 
   * @param email the {@link Email} to be converted
   * @return the converted {@link EmailOutput}
   */
  public static EmailOutput convert(Email email) {
    log.debug("convert(Email email) - start");
    EmailOutput returnValue = modelMapper.map(email, EmailOutput.class);
    log.debug("convert(Email email) - end");
    return returnValue;
  }

  /**
   * Converts from {@link org.crue.hercules.sgi.com.model.Status} to
   * {@link Status}.
   * 
   * @param status the {@link org.crue.hercules.sgi.com.model.Status} to be
   *               converted
   * @return the converted {@link Status}
   */
  public static Status convert(org.crue.hercules.sgi.com.model.Status status) {
    log.debug("convert(org.crue.hercules.sgi.com.model.Status status) - start");
    Status returnValue = modelMapper.map(status, Status.class);
    log.debug("convert(org.crue.hercules.sgi.com.model.Status status) - end");
    return returnValue;
  }

  /**
   * Converts from {@link org.crue.hercules.sgi.com.model.Param} to
   * {@link Param}.
   * 
   * @param param the {@link org.crue.hercules.sgi.com.model.Param} to be
   *              converted
   * @return the converted {@link Param}
   */
  public static Param convert(org.crue.hercules.sgi.com.model.Param param) {
    log.debug("convert(org.crue.hercules.sgi.com.model.Param param) - start");
    Param returnValue = modelMapper.map(param, Param.class);
    log.debug("convert(org.crue.hercules.sgi.com.model.Param param) - end");
    return returnValue;
  }

  /**
   * Converts a {@link Page} of {@link org.crue.hercules.sgi.com.model.Param} to a
   * {@link Page} of {@link Param}.
   * 
   * @param page the {@link Page} of {@link org.crue.hercules.sgi.com.model.Param}
   *             to be converted
   * @return the converted {@link Page} of {@link Param}
   */
  public static Page<Param> convertParamPage(Page<org.crue.hercules.sgi.com.model.Param> page) {
    log.debug("convertParamPage(Page<org.crue.hercules.sgi.com.model.Param> page) - start");
    List<Param> content = page.getContent().stream().map(TypeConverter::convert)
        .collect(Collectors.toList());
    Page<Param> returnValue = new PageImpl<>(content, page.getPageable(), page.getTotalElements());
    log.debug("convertParamPage(Page<org.crue.hercules.sgi.com.model.Param> page) - end");
    return returnValue;
  }

  public static List<org.crue.hercules.sgi.com.model.Recipient> convertRecipients(List<Recipient> recipients) {
    return convertRecipients(null, recipients);
  }

  public static List<org.crue.hercules.sgi.com.model.Recipient> convertRecipients(Long emailId,
      List<Recipient> recipients) {
    log.debug("convertRecipients(Long emailId, List<Recipient> recipients) - start");
    if (CollectionUtils.isEmpty(recipients)) {
      return Collections.emptyList();
    }
    List<org.crue.hercules.sgi.com.model.Recipient> returnValue = recipients.stream().map(r -> convert(emailId, r))
        .collect(Collectors.toList());
    log.debug("convertRecipients(Long emailId, List<Recipient> recipients) - end");
    return returnValue;
  }

  public static org.crue.hercules.sgi.com.model.Recipient convert(Recipient recipient) {
    log.debug("convert(Recipient recipient) - start");
    org.crue.hercules.sgi.com.model.Recipient returnValue = convert(null, recipient);
    log.debug("convert(Recipient recipient) - end");
    return returnValue;
  }

  public static org.crue.hercules.sgi.com.model.Recipient convert(Long emailId, Recipient recipient) {
    log.debug("convert(Long emailId, Recipient recipient) - start");
    org.crue.hercules.sgi.com.model.Recipient returnValue = modelMapper.map(recipient,
        org.crue.hercules.sgi.com.model.Recipient.class);
    returnValue.setEmailId(emailId);
    log.debug("convert(Long emailId, Recipient recipient) - end");
    return returnValue;
  }

  public static List<Recipient> convertRecipientEntities(List<org.crue.hercules.sgi.com.model.Recipient> recipients) {
    log.debug("convertRecipientEntities(List<org.crue.hercules.sgi.com.model.Recipient> recipients) - start");
    List<Recipient> returnValue = null;
    if (CollectionUtils.isEmpty(recipients)) {
      returnValue = Collections.emptyList();
    } else {
      returnValue = recipients.stream().map(TypeConverter::convert)
          .collect(Collectors.toList());
    }
    log.debug("convertRecipientEntities(List<org.crue.hercules.sgi.com.model.Recipient> recipients) - end");
    return returnValue;
  }

  public static Recipient convert(org.crue.hercules.sgi.com.model.Recipient recipient) {
    log.debug("convert(org.crue.hercules.sgi.com.model.Recipient recipient) - start");
    Recipient returnValue = modelMapper.map(recipient, Recipient.class);
    log.debug("convert(org.crue.hercules.sgi.com.model.Recipient recipient) - end");
    return returnValue;
  }

  public static List<Attachment> convertAttachments(List<String> attachments) {
    log.debug("convertAttachments(List<String> attachments) - start");
    List<Attachment> returnValue = convertAttachments(null, attachments);
    log.debug("convertAttachments(List<String> attachments) - end");
    return returnValue;
  }

  public static List<Attachment> convertAttachments(Long emailId, List<String> attachments) {
    log.debug("convertAttachments(Long emailId, List<String> attachments) - start");
    List<Attachment> returnValue = null;
    if (CollectionUtils.isEmpty(attachments)) {
      returnValue = Collections.emptyList();
    } else {
      returnValue = attachments.stream().map(a -> convert(emailId, a))
          .collect(Collectors.toList());
    }
    log.debug("convertAttachments(Long emailId, List<String> attachments) - start");
    return returnValue;
  }

  public static Attachment convert(String attachment) {
    log.debug("convert(String attachment) - start");
    Attachment returnValue = convert(null, attachment);
    log.debug("convert(String attachment) - end");
    return returnValue;
  }

  public static Attachment convert(Long emailId, String attachment) {
    log.debug("convert(Long emailId, String attachment) - start");
    Attachment returnValue = Attachment.builder().emailId(emailId).documentRef(attachment).build();
    log.debug("convert(Long emailId, String attachment) - end");
    return returnValue;
  }

  public static List<org.crue.hercules.sgi.com.model.EmailParam> convertEmailParams(List<EmailParam> params) {
    log.debug("convertEmailParams(List<EmailParam> params) - start");
    List<org.crue.hercules.sgi.com.model.EmailParam> returnValue = convertEmailParams(null, params);
    log.debug("convertEmailParams(List<EmailParam> params) - end");
    return returnValue;
  }

  public static List<org.crue.hercules.sgi.com.model.EmailParam> convertEmailParams(Long emailId,
      List<EmailParam> params) {
    log.debug("convertEmailParams(Long emailId, List<EmailParam> params) - start");
    List<org.crue.hercules.sgi.com.model.EmailParam> retunValue = null;
    if (CollectionUtils.isEmpty(params)) {
      retunValue = Collections.emptyList();
    } else {
      retunValue = params.stream().map(p -> convert(emailId, p))
          .collect(Collectors.toList());
    }
    log.debug("convertEmailParams(Long emailId, List<EmailParam> params) - end");
    return retunValue;
  }

  public static org.crue.hercules.sgi.com.model.EmailParam convert(EmailParam param) {
    log.debug("convert(EmailParam param) - start");
    org.crue.hercules.sgi.com.model.EmailParam returnValue = convert(null, param);
    log.debug("convert(EmailParam param) - end");
    return returnValue;
  }

  public static org.crue.hercules.sgi.com.model.EmailParam convert(Long emailId, EmailParam param) {
    log.debug("convert(Long emailId, EmailParam param) - start");
    org.crue.hercules.sgi.com.model.EmailParam returnValue = modelMapper.map(param,
        org.crue.hercules.sgi.com.model.EmailParam.class);
    EmailParamPK pk = returnValue.getPk();
    if (pk == null) {
      pk = EmailParamPK.builder().emailId(emailId).build();
    }
    returnValue.setPk(pk);
    log.debug("convert(Long emailId, EmailParam param) - end");
    return returnValue;
  }

  public static List<EmailParam> convertEmailParamEntities(List<org.crue.hercules.sgi.com.model.EmailParam> params) {
    log.debug("convertEmailParamEntities(List<org.crue.hercules.sgi.com.model.EmailParam> params) - start");
    List<EmailParam> returnValue = null;
    if (CollectionUtils.isEmpty(params)) {
      returnValue = Collections.emptyList();
    } else {
      returnValue = params.stream().map(TypeConverter::convert)
          .collect(Collectors.toList());
    }
    log.debug("convertEmailParamEntities(List<org.crue.hercules.sgi.com.model.EmailParam> params) - end");
    return returnValue;
  }

  public static EmailParam convert(org.crue.hercules.sgi.com.model.EmailParam param) {
    log.debug("convert(org.crue.hercules.sgi.com.model.EmailParam param) - start");
    EmailParam returnValue = modelMapper.map(param, EmailParam.class);
    log.debug("convert(org.crue.hercules.sgi.com.model.EmailParam param) - end");
    return returnValue;
  }

  public static List<String> convertAttachmentEntities(List<Attachment> attachments) {
    log.debug("convertAttachmentEntities(List<Attachment> attachments) - start");
    List<String> returnValue = null;
    if (CollectionUtils.isEmpty(attachments)) {
      returnValue = Collections.emptyList();
    } else {
      returnValue = attachments.stream().map(TypeConverter::convert)
          .collect(Collectors.toList());
    }
    log.debug("convertAttachmentEntities(List<Attachment> attachments) - end");
    return returnValue;
  }

  public static String convert(Attachment attachment) {
    log.debug("convert(Attachment attachment) - start");
    String returnValue = attachment.getDocumentRef();
    log.debug("convert(Attachment attachment) - end");
    return returnValue;
  }
}
