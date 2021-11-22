package org.crue.hercules.sgi.rep.service.eti;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.http.HttpMethod;
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
      throw new GetDataReportException();
    }

    return formulario;
  }
}
