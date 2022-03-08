package org.crue.hercules.sgi.com.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.crue.hercules.sgi.com.converter.TypeConverter;
import org.crue.hercules.sgi.com.dto.EmailInput;
import org.crue.hercules.sgi.com.dto.EmailOutput;
import org.crue.hercules.sgi.com.dto.Status;
import org.crue.hercules.sgi.com.model.Email;
import org.crue.hercules.sgi.com.service.AttachmentService;
import org.crue.hercules.sgi.com.service.EmailParamService;
import org.crue.hercules.sgi.com.service.EmailService;
import org.crue.hercules.sgi.com.service.RecipientService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
 * Controller for {@link Email} data.
 */
@RestController
@RequestMapping(EmailController.MAPPING)
@Slf4j
public class EmailController {
  /** The URL path delimiter */
  public static final String PATH_DELIMITER = "/";
  /** The controller base path mapping */
  public static final String MAPPING = PATH_DELIMITER + "emails";
  /** The path used to request by name */
  public static final String PATH_ID = PATH_DELIMITER + "{id}";

  /** Email service */
  private final EmailService service;
  /** EmailParam service */
  private final EmailParamService emailParamService;
  /** Attachment service */
  private final AttachmentService attachmentService;
  /** Recipient service */
  private final RecipientService recipientService;

  /**
   * Creates a new EmailController instance.
   * 
   * @param service           {@link EmailService}
   * @param emailParamService {@link EmailParamService}
   * @param attachmentService {@link AttachmentService}
   * @param recipientService  {@link RecipientService}
   */
  public EmailController(EmailService service, EmailParamService emailParamService, AttachmentService attachmentService,
      RecipientService recipientService) {
    log.debug(
        "EmailController(EmailService service, EmailParamService emailParamService, AttachmentService attachmentService, RecipientService recipientService) - start");
    this.service = service;
    this.emailParamService = emailParamService;
    this.attachmentService = attachmentService;
    this.recipientService = recipientService;
    log.debug(
        "EmailController(EmailService service, EmailParamService emailParamService, AttachmentService attachmentService, RecipientService recipientService) - end");
  }

  /**
   * Create {@link Email}.
   * 
   * @param email {@link Email} to create
   * @return the newly created {@link Email}
   */
  @PostMapping()
  public ResponseEntity<EmailOutput> create(@Valid @RequestBody EmailInput email) {
    log.debug("create(@Valid @RequestBody EmailInput email) - start");
    Email created = service.create(TypeConverter.convert(email), TypeConverter.convertRecipients(email.getRecipients()),
        TypeConverter.convertAttachments(email.getAttachments()), TypeConverter.convertEmailParams(email.getParams()));

    EmailOutput returnValue = buildEmailOutput(created);
    log.debug("create(@Valid @RequestBody EmailInput email) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.CREATED);
  }

  /**
   * Update existing {@link Email}.
   * 
   * @param id    {@link Email} id
   * @param email {@link Email} data
   * @return the updated {@link Email}
   */
  @PutMapping(PATH_ID)
  public ResponseEntity<EmailOutput> update(@PathVariable Long id, @Valid @RequestBody EmailInput email) {
    log.debug("update(Long id, EmailInput email) - start");
    Email updated = service.update(TypeConverter.convert(id, email),
        TypeConverter.convertRecipients(id, email.getRecipients()),
        TypeConverter.convertAttachments(id, email.getAttachments()),
        TypeConverter.convertEmailParams(id, email.getParams()));
    EmailOutput returnValue = buildEmailOutput(updated);
    log.debug("update(Long id, EmailInput email) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Delete existing {@link Email}.
   * 
   * @param id {@link Email} id
   */
  @DeleteMapping(PATH_ID)
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    log.debug("delete(Long id) - start");
    service.delete(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Get {@link Email}.
   * 
   * @param id {@link Email} id
   * @return the {@link Email}
   */
  @GetMapping(PATH_ID)
  public ResponseEntity<EmailOutput> get(@PathVariable Long id) {
    log.debug("get(@PathVariable Long id) - start");
    Email email = service.get(id);
    EmailOutput returnValue = buildEmailOutput(email);
    log.debug("get(@PathVariable Long id) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Get paged and filtered {@link Email} data.
   * 
   * @param query  query filter
   * @param paging paging info
   * @return the requested data
   */
  @GetMapping()
  public ResponseEntity<Page<EmailOutput>> findAll(@RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Page<Email> page = service.findAll(query, paging);

    if (page.isEmpty()) {
      log.debug("findAll(String query,Pageable paging) - end");
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    Page<EmailOutput> returnValue = buildEmailOutput(page);
    log.debug("findAll(String query,Pageable paging) - end");
    return new ResponseEntity<>(returnValue, HttpStatus.OK);
  }

  /**
   * Sends the {@link Email} with the given id.
   * 
   * @param id {@link Email} identifier.
   * @return the {@link Status} of sending the {@link Email}
   */
  @GetMapping("/{id}/send")
  public Status send(@PathVariable Long id) {
    log.debug("send(Long id) - start");
    org.crue.hercules.sgi.com.model.Status status = service.send(id);
    Status returnValue = TypeConverter.convert(status);
    log.debug("send(Long id) - end");
    return returnValue;
  }

  private Page<EmailOutput> buildEmailOutput(Page<Email> page) {
    List<EmailOutput> content = page.getContent().stream().map(this::buildEmailOutput).collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  private EmailOutput buildEmailOutput(Email email) {
    EmailOutput returnValue = TypeConverter.convert(email);
    returnValue.setParams(TypeConverter.convertEmailParamEntities(emailParamService.findByEmailId(email.getId())));
    returnValue.setAttachments(TypeConverter.convertAttachmentEntities(attachmentService.findByEmailId(email.getId())));
    returnValue.setRecipients(TypeConverter.convertRecipientEntities(recipientService.findByEmailId(email.getId())));
    return returnValue;
  }
}
