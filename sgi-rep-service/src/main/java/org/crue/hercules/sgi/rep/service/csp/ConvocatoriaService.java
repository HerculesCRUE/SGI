package org.crue.hercules.sgi.rep.service.csp;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.csp.ConvocatoriaDto;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConvocatoriaService extends BaseRestTemplateService<ConvocatoriaDto> {
  private static final String URL_API = "/convocatorias";

  public ConvocatoriaService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getCspUrl(), restTemplate);
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }
}
