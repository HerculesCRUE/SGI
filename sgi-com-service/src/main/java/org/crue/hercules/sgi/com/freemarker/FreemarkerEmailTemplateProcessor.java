package org.crue.hercules.sgi.com.freemarker;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.com.dto.ProcessedEmailTpl;
import org.crue.hercules.sgi.com.exceptions.ContentException;
import org.crue.hercules.sgi.com.exceptions.SubjectException;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.i18n.I18nConfig;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.core.StringArraySequence;
import freemarker.template.AttemptExceptionReporter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateSequenceModel;
import lombok.extern.slf4j.Slf4j;

/**
 * Allows interpollating an Email template with the given name using the
 * provided parameters.
 */
@Slf4j
public class FreemarkerEmailTemplateProcessor {
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String MESSAGE_KEY_ID = "id";
  private EmailTplRepository repository;
  private Configuration freemarkerCfg;

  public FreemarkerEmailTemplateProcessor(EmailTplRepository repository, Configuration freemarkerCfg) {
    log.debug(
        "FreemarkerEmailTemplateProcessor(EmailTplRepository repository, Configuration freemarkerCfg) - start");
    this.repository = repository;
    this.freemarkerCfg = freemarkerCfg;
    log.debug(
        "FreemarkerEmailTemplateProcessor(EmailTplRepository repository, Configuration freemarkerCfg) - end");
  }

  private void configure() {
    freemarkerCfg.setAttemptExceptionReporter(AttemptExceptionReporter.LOG_WARN_REPORTER);
    setSharedVariables();
  }

  private void setSharedVariables() {
    TemplateSequenceModel languagePriorities = new StringArraySequence(
        I18nConfig.get().getLanguagePriorities().stream().map(Language::getCode).toArray(String[]::new));
    freemarkerCfg.setSharedVariable("languagePriorities", languagePriorities);
  }

  public ProcessedEmailTpl processTemplate(String name, Map<String, Object> params)
      throws ContentException, SubjectException {
    log.debug(
        "processTemplate(String name, Map<String, Object> params) - start");
    EmailTpl emailTpl = repository.findByName(
        name)
        .orElseThrow(() -> new NotFoundException(ProblemMessage.builder().key(NotFoundException.class)
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY, ApplicationContextSupport.getMessage(EmailTpl.class))
            .parameter(MESSAGE_KEY_ID, name).build()));
    ProcessedEmailTpl.ProcessedEmailTplBuilder builder = ProcessedEmailTpl.builder();
    this.configure();
    try {
      if (ObjectUtils.isNotEmpty(emailTpl.getSubjectTpl())) {
        Template subjectTemplate = freemarkerCfg.getTemplate(
            name + FreemarkerDatabaseEmailTemplateLoader.PATH_SUBJECT, LocaleContextHolder.getLocale());
        builder.subject(FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplate,
            params));
      }
    } catch (IOException | TemplateException | RuntimeException e) {
      throw new SubjectException(e);
    }
    try {
      if (ObjectUtils.isNotEmpty(emailTpl.getContentTpl())) {
        if (ObjectUtils.isNotEmpty(emailTpl.getContentTpl().getTplText())) {
          Template textContentTemplate = freemarkerCfg.getTemplate(
              name + FreemarkerDatabaseEmailTemplateLoader.PATH_CONTENT_TEXT, LocaleContextHolder.getLocale());
          builder.contentText(FreeMarkerTemplateUtils.processTemplateIntoString(textContentTemplate,
              params));
        }
        if (ObjectUtils.isNotEmpty(emailTpl.getContentTpl().getTplHtml())) {
          Template htmlContentTemplate = freemarkerCfg.getTemplate(
              name + FreemarkerDatabaseEmailTemplateLoader.PATH_CONTENT_HTML, LocaleContextHolder.getLocale());
          builder.contentHtml(FreeMarkerTemplateUtils.processTemplateIntoString(
              htmlContentTemplate,
              params));
        }
      }
      ProcessedEmailTpl returnValue = builder.build();
      log.debug(
          "processTemplate(String name, Map<String, Object> params) - end");
      return returnValue;
    } catch (IOException | TemplateException | RuntimeException e) {
      throw new ContentException(e);
    }
  }
}
