package org.crue.hercules.sgi.rep.service.eti;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiBaseService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormularioService extends BaseRestTemplateService<FormularioDto> {
  private static final String URL_API = "/formularios";

  public FormularioService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }

  public FormularioDto findByMemoriaId(Long idMemoria) {
    FormularioDto formulario = null;
    try {
      final ResponseEntity<FormularioDto> responseFormulario = getRestTemplate().exchange(
          getUrlBase() + URL_API + "/" + idMemoria + "/memoria", HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), FormularioDto.class);

      formulario = responseFormulario.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }

    return formulario;
  }

  /**
   * Devuelve el report asociado a un formulario en el idioma indicado
   *
   * @param idFormulario identificador del formulario
   * @param lang         idioma
   * @return el report
   */
  public byte[] getReport(Long idFormulario, Language lang) {
    byte[] resource = null;
    try {
      HttpMethod httpMethod = HttpMethod.GET;
      String requestUrl = getUrlBase() + URL_API + "/" + idFormulario + "/report?l=" + lang.getCode();

      HttpEntity<Void> request = new HttpEntityBuilder<Void>()
          .withClientAuthorization(SgiApiBaseService.CLIENT_REGISTRATION_ID).build();
      resource = getRestTemplate().exchange(
          requestUrl,
          httpMethod,
          request, new ParameterizedTypeReference<byte[]>() {
          }).getBody();
      return resource;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }

  /**
   * Comprueba la existencia de un report asocidado al formulario en el idioma
   * indicado
   * 
   * @param idFormulario identificador del formulario
   * @param lang         idoma
   * @return <code>true</code> si existe un report asociado
   */
  public boolean existsReport(Long idFormulario, Language lang) {
    ResponseEntity<Void> response = null;
    try {
      HttpMethod httpMethod = HttpMethod.HEAD;
      String requestUrl = getUrlBase() + URL_API + "/" + idFormulario + "/report?l=" + lang.getCode();

      HttpEntity<Void> request = new HttpEntityBuilder<Void>()
          .withClientAuthorization(SgiApiBaseService.CLIENT_REGISTRATION_ID).build();
      response = getRestTemplate().exchange(
          requestUrl,
          httpMethod,
          request, new ParameterizedTypeReference<Void>() {
          });
      return response.getStatusCode().equals(HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }

  }
}
