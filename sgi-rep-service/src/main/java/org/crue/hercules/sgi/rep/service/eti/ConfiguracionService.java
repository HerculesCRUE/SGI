package org.crue.hercules.sgi.rep.service.eti;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.ConfiguracionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfiguracionService extends BaseRestTemplateService<ConfiguracionDto> {
  private static final String URL_API = "/configuraciones";

  public ConfiguracionService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  public ConfiguracionDto findConfiguracion() {
    ConfiguracionDto configuracion = null;
    try {
      final ResponseEntity<ConfiguracionDto> responseFormulario = getRestTemplate().exchange(getUrlBase() + URL_API,
          HttpMethod.GET, new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), ConfiguracionDto.class);

      configuracion = responseFormulario.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return configuracion;
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }
}
