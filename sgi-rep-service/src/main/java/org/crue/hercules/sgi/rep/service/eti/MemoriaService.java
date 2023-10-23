package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.List;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaInput;
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
public class MemoriaService extends SgiApiBaseService {
  private static final String PATH_DELIMITER = "/";
  private static final String URL_API = "/memorias";

  private static final String PATH_ID = URL_API + PATH_DELIMITER + "{id}";

  private static final String PATH_RESPUESTAS = PATH_ID + PATH_DELIMITER + "respuestas";

  public MemoriaService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public MemoriaDto findById(Long id) {
    log.debug("findById({}) - start", id);
    MemoriaDto memoria = null;
    try {
      ServiceType serviceType = ServiceType.ETI;
      String relativeUrl = PATH_ID;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      memoria = super.<MemoriaDto>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<MemoriaDto>() {
          }, id).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    log.debug("findById({}) - end", id);
    return memoria;
  }

  public List<RespuestaInput> getRespuestas(Long id) {
    log.debug("getRespuestas({}) - start", id);
    List<RespuestaInput> respuestas = new ArrayList<>();
    try {

      ServiceType serviceType = ServiceType.ETI;
      String relativeUrl = PATH_RESPUESTAS;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      respuestas = super.<List<RespuestaInput>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<RespuestaInput>>() {
          }, id).getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getRespuestas({}) - end", id);
    return respuestas;
  }

}
