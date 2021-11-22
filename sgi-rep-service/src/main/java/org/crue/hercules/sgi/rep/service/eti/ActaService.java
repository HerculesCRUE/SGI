package org.crue.hercules.sgi.rep.service.eti;

import java.net.URI;
import java.util.List;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaDto;
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
public class ActaService extends BaseRestTemplateService<ActaDto> {
  private static final String URL_API = "/actas";

  public ActaService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  /**
   * Devuelve el número de evaluaciones nuevas asociadas a un {@link ActaDto}
   *
   * @param idActa Id de {@link ActaDto}.
   * @return número de evaluaciones nuevas
   */
  public Long countEvaluacionesNuevas(Long idActa) {
    return countEvaluaciones(idActa, "/numero-evaluaciones-nuevas");
  }

  /**
   * Devuelve el número de evaluaciones de revisión sin las de revisión mínima
   *
   * @param idActa Id de {@link ActaDto}.
   * @return número de evaluaciones
   */
  public Long countEvaluacionesRevisionSinMinima(Long idActa) {
    return countEvaluaciones(idActa, "/numero-evaluaciones-revision-sin-minima");
  }

  private Long countEvaluaciones(Long idActa, String endPoint) {
    Long numEvaluaciones = null;
    try {
      final ResponseEntity<Long> responseFormulario = getRestTemplate().exchange(
          getUrlBase() + URL_API + "/" + idActa + endPoint, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Long.class);

      numEvaluaciones = responseFormulario.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return numEvaluaciones;
  }

  /**
   * Devuelve una lista de {@link MemoriaEvaluadaDto} sin las de revisión mínima
   * para una determinada {@link ActaDto}
   * 
   * @param idActa Id de {@link ActaDto}.
   * @return lista de memorias evaluadas
   */
  public List<MemoriaEvaluadaDto> findAllMemoriasEvaluadasSinRevMinimaByActaId(Long idActa) {
    List<MemoriaEvaluadaDto> resultados = null;
    try {

      String sort = "numReferencia,asc";
      URI uri = UriComponentsBuilder.fromUriString(getUrlBase() + getUrlApi() + "/" + idActa + "/memorias-evaluadas")
          .queryParam("s", sort).build(false).toUri();

      final ResponseEntity<List<MemoriaEvaluadaDto>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<MemoriaEvaluadaDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }
}
