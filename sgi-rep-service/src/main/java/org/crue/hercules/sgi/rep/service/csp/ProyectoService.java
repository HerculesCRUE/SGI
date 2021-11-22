package org.crue.hercules.sgi.rep.service.csp;

import java.net.URI;
import java.util.List;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.csp.ProyectoDto;
import org.crue.hercules.sgi.rep.dto.csp.ProyectoEntidadConvocanteDto;
import org.crue.hercules.sgi.rep.dto.csp.ProyectoEntidadFinanciadoraDto;
import org.crue.hercules.sgi.rep.dto.csp.ProyectoEquipoDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseRestTemplateService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProyectoService extends BaseRestTemplateService<ProyectoDto> {
  private static final String SORT_ID_ASC = "id,asc";
  private static final String URL_API = "/proyectos";

  public ProyectoService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getCspUrl(), restTemplate);
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }

  public List<ProyectoEquipoDto> findAllProyectoEquipo(Long idProyecto) {
    List<ProyectoEquipoDto> resultados = null;
    try {

      String sort = SORT_ID_ASC;
      URI uri = UriComponentsBuilder.fromUriString(getUrlBase() + getUrlApi() + "/" + idProyecto + "/proyectoequipos")
          .queryParam("s", sort).build(false).toUri();

      final ResponseEntity<List<ProyectoEquipoDto>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          new HttpEntityBuilder<ProyectoEquipoDto>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<ProyectoEquipoDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }

  public List<ProyectoEntidadConvocanteDto> findAllProyectoEntidadConvocante(Long idProyecto) {
    List<ProyectoEntidadConvocanteDto> resultados = null;
    try {

      String sort = SORT_ID_ASC;
      URI uri = UriComponentsBuilder
          .fromUriString(getUrlBase() + getUrlApi() + "/" + idProyecto + "/entidadconvocantes").queryParam("s", sort)
          .build(false).toUri();

      final ResponseEntity<List<ProyectoEntidadConvocanteDto>> response = getRestTemplate().exchange(uri,
          HttpMethod.GET, new HttpEntityBuilder<ProyectoEntidadConvocanteDto>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<ProyectoEntidadConvocanteDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }

  public List<ProyectoEntidadFinanciadoraDto> findAllProyectoEntidadFinanciadora(Long idProyecto) {
    List<ProyectoEntidadFinanciadoraDto> resultados = null;
    try {

      String sort = SORT_ID_ASC;
      URI uri = UriComponentsBuilder
          .fromUriString(getUrlBase() + getUrlApi() + "/" + idProyecto + "/proyectoentidadfinanciadoras")
          .queryParam("s", sort).build(false).toUri();

      final ResponseEntity<List<ProyectoEntidadFinanciadoraDto>> response = getRestTemplate().exchange(uri,
          HttpMethod.GET,
          new HttpEntityBuilder<ProyectoEntidadFinanciadoraDto>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<ProyectoEntidadFinanciadoraDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }
}
