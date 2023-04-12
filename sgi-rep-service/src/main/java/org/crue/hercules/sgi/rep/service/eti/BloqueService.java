package org.crue.hercules.sgi.rep.service.eti;

import java.net.URI;
import java.util.List;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.BloqueDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BloqueService extends BaseRestTemplateService<BloqueDto> {
  public static final String URL_API = "/bloques";

  public BloqueService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  public List<BloqueDto> findByFormularioId(Long idFormulario) {
    List<BloqueDto> result = null;
    try {

      String sort = "orden,asc";
      URI uri = UriComponentsBuilder.fromUriString(getUrlBase() + getUrlApi() + "/" + idFormulario + "/formulario")
          .queryParam("s", sort).build(false).toUri();
      result = findAllFromURI(uri, new HttpHeaders(), new ParameterizedTypeReference<List<BloqueDto>>() {
      });

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return result;
  }

  public BloqueDto getBloqueComentariosGenerales() {
    BloqueDto result = null;
    try {
      URI uri = UriComponentsBuilder.fromUriString(getUrlBase() + getUrlApi() + "/comentarios-generales").build(false)
          .toUri();
      result = findFromURI(uri, new HttpHeaders(), new ParameterizedTypeReference<BloqueDto>() {
      });

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return result;
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }
}
