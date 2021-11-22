package org.crue.hercules.sgi.rep.service.eti;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RespuestaService extends BaseRestTemplateService<RespuestaDto> {
  private static final String URL_API = "/respuestas";

  public RespuestaService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }

  public RespuestaDto findByMemoriaIdAndApartadoId(Long idMemoria, Long idApartado) {
    RespuestaDto formulario = null;
    try {
      final ResponseEntity<RespuestaDto> responseFormulario = getRestTemplate().exchange(
          getUrlBase() + URL_API + "/" + idMemoria + "/" + idApartado, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), RespuestaDto.class);

      formulario = responseFormulario.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return formulario;
  }
}
