package org.crue.hercules.sgi.eti.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.exceptions.rep.MicroserviceCallException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Service de llamada a microservicio de reporting
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class SgdocService {

  private static final String URL_API = "/documentos";

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  public SgdocService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  public DocumentoOutput uploadInforme(String fileName, Resource informePdf) {
    log.debug("uploadInforme(fileName, informePdf)- start");

    DocumentoOutput documento = null;
    try {

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      headers
          .setAcceptLanguage(Arrays.asList(new Locale.LanguageRange(LocaleContextHolder.getLocale().toLanguageTag())));

      HttpServletRequest httpServletRequest = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
          .filter(ServletRequestAttributes.class::isInstance).map(ServletRequestAttributes.class::cast)
          .map(ServletRequestAttributes::getRequest).orElseThrow(MicroserviceCallException::new);
      String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
      headers.set(HttpHeaders.AUTHORIZATION, authorization);

      MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
      bodyMap.add("archivo", new MultipartInputStreamResource(informePdf.getInputStream(), fileName));

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

      ResponseEntity<DocumentoOutput> responseDocumento = restTemplate
          .exchange(restApiProperties.getSgdocUrl() + URL_API, HttpMethod.POST, requestEntity, DocumentoOutput.class);

      documento = responseDocumento.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (documento == null) {
      throw new MicroserviceCallException();
    }

    return documento;
  }

  public DocumentoOutput getDocumento(String documentRef) {
    log.debug("getDocumento(String documentRef) - start");
    String relativeUrl = "/documentos/{documentRef}";
    HttpMethod httpMethod = HttpMethod.GET;
    DocumentoOutput documento = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);
      headers
          .setAcceptLanguage(Arrays.asList(new Locale.LanguageRange(LocaleContextHolder.getLocale().toLanguageTag())));

      HttpServletRequest httpServletRequest = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
          .filter(ServletRequestAttributes.class::isInstance).map(ServletRequestAttributes.class::cast)
          .map(ServletRequestAttributes::getRequest).orElseThrow(MicroserviceCallException::new);
      String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
      headers.set(HttpHeaders.AUTHORIZATION, authorization);

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(null, headers);

      String mergedURL = new StringBuilder(restApiProperties.getSgdocUrl()).append(relativeUrl).toString();

      documento = restTemplate.exchange(mergedURL, httpMethod,
          requestEntity,
          new ParameterizedTypeReference<DocumentoOutput>() {
          }, documentRef).getBody();
    } catch (Exception e) {
      log.error("getDocumento(String documentRef) - error" + e.getMessage(), e);
    }

    if (documento == null) {
      throw new MicroserviceCallException();
    }

    return documento;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  class MultipartInputStreamResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamResource(InputStream inputStream, String filename) {
      super(inputStream);
      this.filename = filename;
    }

    @Override
    public long contentLength() {
      return -1;
    }
  }
}