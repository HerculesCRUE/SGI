package org.crue.hercules.sgi.rep.service.eti;

import java.net.URI;
import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
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
public class EvaluacionService extends BaseRestTemplateService<EvaluacionDto> {
  private static final String URL_API = "/evaluaciones";

  public EvaluacionService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  public List<ComentarioDto> findByEvaluacionIdGestor(Long idEvaluacion) {
    String endPoint = "/comentarios-gestor";
    return findComentarios(idEvaluacion, endPoint);
  }

  public List<ComentarioDto> findByEvaluacionIdEvaluador(Long idEvaluacion) {
    String endPoint = "/comentarios-evaluador";
    return findComentarios(idEvaluacion, endPoint);
  }

  private List<ComentarioDto> findComentarios(Long idEvaluacion, String endPoint) {
    List<ComentarioDto> resultados = null;
    try {

      String sort = "apartado.id,asc";
      URI uri = UriComponentsBuilder.fromUriString(getUrlBase() + getUrlApi() + "/" + idEvaluacion + endPoint)
          .queryParam("s", sort).build(false).toUri();

      final ResponseEntity<List<ComentarioDto>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<ComentarioDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }

  public Integer countByEvaluacionIdAndTipoComentarioId(Long idEvaluacion, Long tipoComentario) {
    Integer numComentarios = null;
    try {
      final ResponseEntity<Integer> responseFormulario = getRestTemplate().exchange(
          getUrlBase() + URL_API + "/" + idEvaluacion + "/" + tipoComentario + "/numero-comentarios", HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Integer.class);

      numComentarios = responseFormulario.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return numComentarios;
  }

  public String findIdPresidenteByIdEvaluacion(Long idEvaluacion) {
    String presidente = null;
    try {
      final ResponseEntity<String> responseFormulario = getRestTemplate().exchange(
          getUrlBase() + URL_API + "/" + idEvaluacion + "/presidente", HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), String.class);

      presidente = responseFormulario.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return presidente;
  }

  /**
   * Retorna la primera fecha de envío a secretaría (histórico estado)
   * 
   * @param idEvaluacion Id de {@link EvaluacionDto}.
   * @return fecha de envío a secretaría
   */
  public Instant findFirstFechaEnvioSecretariaByIdEvaluacion(Long idEvaluacion) {
    Instant fechaEnvioSecretaria = null;
    try {
      final ResponseEntity<Instant> responseFormulario = getRestTemplate().exchange(
          getUrlBase() + URL_API + "/" + idEvaluacion + "/primera-fecha-envio-secretaria", HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Instant.class);

      fechaEnvioSecretaria = responseFormulario.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return fechaEnvioSecretaria;
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }
}
