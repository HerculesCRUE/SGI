package org.crue.hercules.sgi.rep.service.eti;

import java.net.URI;
import java.util.List;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.AsistentesDto;
import org.crue.hercules.sgi.rep.dto.eti.ConvocatoriaReunionDto;
import org.crue.hercules.sgi.rep.dto.eti.DictamenDto;
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
public class ConvocatoriaReunionService extends BaseRestTemplateService<ConvocatoriaReunionDto> {
  private static final String URL_API = "/convocatoriareuniones";

  public ConvocatoriaReunionService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties.getEtiUrl(), restTemplate);
  }

  @Override
  protected String getUrlApi() {
    return URL_API;
  }

  public List<AsistentesDto> findAsistentesByConvocatoriaReunionId(Long idConvocatoriaReunion) {
    List<AsistentesDto> resultados = null;
    try {

      String sort = "id,asc";
      URI uri = UriComponentsBuilder
          .fromUriString(getUrlBase() + getUrlApi() + "/" + idConvocatoriaReunion + "/asistentes").queryParam("s", sort)
          .build(false).toUri();

      final ResponseEntity<List<AsistentesDto>> response = getRestTemplate().exchange(uri, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          new ParameterizedTypeReference<List<AsistentesDto>>() {
          });

      resultados = response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return resultados;
  }

  /**
   * Retorna la fecha convocatoria y acta (codigo convocatoria) de la última
   * evaluación de tipo memoria de la memoria original (y que no sea revisión
   * mínima)
   * 
   * @param idEvaluacion Id de la {@link EvaluacionDto}
   * @param idDictamen   Id del {@link DictamenDto}
   * @return ConvocatoriaReunion
   */
  public ConvocatoriaReunionDto findConvocatoriaUltimaEvaluacionTipoMemoria(Long idEvaluacion, Long idDictamen) {
    ConvocatoriaReunionDto convocatoriaUltimaEvaluacion = null;
    try {
      final ResponseEntity<ConvocatoriaReunionDto> responseFormulario = getRestTemplate().exchange(
          getUrlBase() + URL_API + "/" + idEvaluacion + "/" + idDictamen + "/convocatoria-ultima-evaluacion",
          HttpMethod.GET, new HttpEntityBuilder<>().withCurrentUserAuthorization().build(),
          ConvocatoriaReunionDto.class);

      convocatoriaUltimaEvaluacion = responseFormulario.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return convocatoriaUltimaEvaluacion;
  }

}
