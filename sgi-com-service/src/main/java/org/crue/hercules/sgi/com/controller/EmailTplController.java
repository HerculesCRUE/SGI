package org.crue.hercules.sgi.com.controller;

import java.util.HashMap;
import java.util.List;

import org.crue.hercules.sgi.com.converter.TypeConverter;
import org.crue.hercules.sgi.com.dto.Param;
import org.crue.hercules.sgi.com.dto.ProcessedEmailTpl;
import org.crue.hercules.sgi.com.exceptions.ContentException;
import org.crue.hercules.sgi.com.exceptions.SubjectException;
import org.crue.hercules.sgi.com.freemarker.FreemarkerEmailTemplateProcessor;
import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.service.ParamService;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@link EmailTpl} data.
 */
@RestController
@RequestMapping(EmailTplController.MAPPING)
@Slf4j
public class EmailTplController {
  /** The URL path delimiter */
  public static final String PATH_DELIMITER = "/";
  /** The controller base path mapping */
  public static final String MAPPING = PATH_DELIMITER + "emailtpls";

  private final FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor;
  private final ParamService paramService;

  /**
   * Creates a new EmailTplController instance.
   * 
   * @param freemarkerEmailTemplateProcessor {@link FreemarkerEmailTemplateProcessor}
   * @param paramService                     {@link ParamService}
   */
  public EmailTplController(
      FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor,
      ParamService paramService) {
    log.debug(
        "EmailConverter converter, FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor, ParamService paramService) - start");
    this.freemarkerEmailTemplateProcessor = freemarkerEmailTemplateProcessor;
    this.paramService = paramService;
    log.debug(
        "EmailConverter converter, FreemarkerEmailTemplateProcessor freemarkerEmailTemplateProcessor, ParamService paramService) - end");
  }

  /**
   * Process the named template with the provided parameters.
   * 
   * @param name   {@link EmailTpl} name.
   * @param params the parameters to be replaced in the template
   * @return the processed template
   * @throws SubjectException if there is a problem processing the subject
   * @throws ContentException if there is a problem processing the content
   */
  @RequestMapping(value = "/{name}/process", method = { RequestMethod.GET, RequestMethod.POST })
  public ProcessedEmailTpl processTemplate(@PathVariable String name,
      @RequestBody List<org.crue.hercules.sgi.com.dto.EmailParam> params) throws ContentException, SubjectException {
    log.debug(
        "processTemplate(@PathVariable String name, List<org.crue.hercules.sgi.com.dto.EmailParam> params) - start");
    HashMap<String, Object> paramsMap = new HashMap<>();
    for (org.crue.hercules.sgi.com.dto.EmailParam param : params) {
      // TODO type conversion
      paramsMap.put(param.getName(), param.getValue());
    }
    ProcessedEmailTpl returnValue = freemarkerEmailTemplateProcessor.processTemplate(name, paramsMap);
    log.debug(
        "processTemplate(@PathVariable String name, List<org.crue.hercules.sgi.com.dto.EmailParam> params) - end");
    return returnValue;
  }

  /**
   * Get the named template parameters.
   * 
   * @param name   {@link EmailTpl} name.
   * @param query  RSQL expression with the restrictions to apply in the search
   * @param paging paging info
   * @return {@link Param} pagged and filtered
   */
  @GetMapping("/{name}/parameters")
  public Page<Param> parameters(@PathVariable String name,
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("parameters(@PathVariable String name, String query, Pageable paging) - start");
    Page<org.crue.hercules.sgi.com.model.Param> params = paramService.findByEmailTplName(name, query, paging);
    Page<Param> returnValue = TypeConverter.convertParamPage(params);
    log.debug("parameters(@PathVariable String name, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Get the named template subject parameters.
   * 
   * @param name   {@link EmailTpl} name.
   * @param query  RSQL expression with the restrictions to apply in the search
   * @param paging paging info
   * @return {@link Param} pagged and filtered
   */
  @GetMapping("/{name}/parameters/subject")
  public Page<Param> subjectParameters(@PathVariable String name,
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("subjectParameters(@PathVariable String name, String query, Pageable paging) - start");
    Page<org.crue.hercules.sgi.com.model.Param> params = paramService.findSubjectTplParamByEmailTplName(name, query,
        paging);
    Page<Param> returnValue = TypeConverter.convertParamPage(params);
    log.debug("subjectParameters(@PathVariable String name, String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Get the named template content parameters.
   * 
   * @param name   {@link EmailTpl} name.
   * @param query  RSQL expression with the restrictions to apply in the search
   * @param paging paging info
   * @return {@link Param} pagged and filtered
   */
  @GetMapping("/{name}/parameters/content")
  public Page<Param> contentParameters(@PathVariable String name,
      @RequestParam(name = "q", required = false) String query,
      @RequestPageable(sort = "s") Pageable paging) {
    log.debug("contentParameters(@PathVariable String name, String query, Pageable paging) - start");
    Page<org.crue.hercules.sgi.com.model.Param> params = paramService.findContentTplParamByEmailTplName(name, query,
        paging);
    Page<Param> returnValue = TypeConverter.convertParamPage(params);
    log.debug("contentParameters(@PathVariable String name, String query, Pageable paging) - end");
    return returnValue;
  }

}
