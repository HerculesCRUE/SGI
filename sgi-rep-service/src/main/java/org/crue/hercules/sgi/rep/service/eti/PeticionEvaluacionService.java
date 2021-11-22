package org.crue.hercules.sgi.rep.service.eti;

import java.net.URI;
import java.util.List;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaPeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.TareaDto;
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
public class PeticionEvaluacionService extends BaseRestTemplateService<PeticionEvaluacionDto> {
  private static final String URL_API = "/peticionevaluaciones";

  public PeticionEvaluacionService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }

  public List<MemoriaPeticionEvaluacionDto> findMemoriaByPeticionEvaluacionMaxVersion(Long idPeticionEvaluacion) {
    List<MemoriaPeticionEvaluacionDto> memorias = null;
    try {
      final ResponseEntity<List<MemoriaPeticionEvaluacionDto>> responseFormularios = getRestTemplate().exchange(
          getUrlBase() + URL_API + "/" + idPeticionEvaluacion + "/memorias", HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<MemoriaPeticionEvaluacionDto>>() {
          });
      memorias = responseFormularios.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return memorias;
  }

  public List<TareaDto> findTareasEquipoTrabajo(Long idPeticionEvaluacion) {
    List<TareaDto> resultados = null;
    try {

      String sort = "id,asc";
      URI uri = UriComponentsBuilder
          .fromUriString(getUrlBase() + getUrlApi() + "/" + idPeticionEvaluacion + "/tareas-equipo-trabajo")
          .queryParam("s", sort).build(false).toUri();

      final ResponseEntity<List<TareaDto>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<TareaDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }
}
