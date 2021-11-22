package org.crue.hercules.sgi.rep.service.eti;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class ComentarioService extends BaseRestTemplateService<ComentarioDto> {
  private static final String URL_API = "/comentarios";

  public ComentarioService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }

}
