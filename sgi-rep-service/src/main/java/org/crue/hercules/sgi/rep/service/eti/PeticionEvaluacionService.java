package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaPeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.TareaDto;
import org.crue.hercules.sgi.rep.enums.ServiceType;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiBaseService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PeticionEvaluacionService extends SgiApiBaseService {
  private static final String PATH_DELIMITER = "/";
  private static final String URL_API = "/peticionevaluaciones";

  private static final String SORT_ID_ASC = "s=id,asc";

  private static final String PATH_ID = URL_API + PATH_DELIMITER + "{id}";
  private static final String PATH_MEMORIAS = PATH_ID + PATH_DELIMITER + "memorias";
  private static final String PATH_TAREAS_EQUIPO_TRABAJO = PATH_ID + PATH_DELIMITER + "tareas-equipo-trabajo?"
      + SORT_ID_ASC;

  public PeticionEvaluacionService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public List<MemoriaPeticionEvaluacionDto> getMemorias(Long peticionEvaluacionId) {
    log.debug("getMemorias({}) - start", peticionEvaluacionId);
    List<MemoriaPeticionEvaluacionDto> memorias = new ArrayList<>();
    try {

      ServiceType serviceType = ServiceType.ETI;
      String relativeUrl = PATH_MEMORIAS;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      memorias = super.<List<MemoriaPeticionEvaluacionDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<MemoriaPeticionEvaluacionDto>>() {
          }, peticionEvaluacionId).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getMemorias({}) - end", peticionEvaluacionId);
    return memorias;
  }

  public List<TareaDto> getTareasEquipoTrabajo(Long peticionEvaluacionId) {
    log.debug("getTareasEquipoTrabajo({}) - start", peticionEvaluacionId);
    List<TareaDto> tareas = new ArrayList<>();
    try {

      ServiceType serviceType = ServiceType.ETI;
      String relativeUrl = PATH_TAREAS_EQUIPO_TRABAJO;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      tareas = super.<List<TareaDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<TareaDto>>() {
          }, peticionEvaluacionId).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getTareasEquipoTrabajo({}) - end", peticionEvaluacionId);
    return tareas;
  }

}
