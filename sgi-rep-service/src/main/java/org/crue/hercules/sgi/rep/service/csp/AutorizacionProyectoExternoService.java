package org.crue.hercules.sgi.rep.service.csp;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionDto;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AutorizacionProyectoExternoService extends BaseRestTemplateService<AutorizacionDto> {
  private static final String URL_API = "/autorizaciones";

  public AutorizacionProyectoExternoService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getCspUrl(), restTemplate);
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }
}
