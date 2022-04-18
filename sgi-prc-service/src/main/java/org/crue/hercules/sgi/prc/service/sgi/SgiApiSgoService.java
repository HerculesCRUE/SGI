package org.crue.hercules.sgi.prc.service.sgi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.sgp.PersonaDto.AreaConocimientoDto;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SgiApiSgoService extends SgiApiBaseService {

  public SgiApiSgoService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public List<AreaConocimientoDto> findAllAreasConocimiento(String query) {
    List<AreaConocimientoDto> result = new ArrayList<>();

    try {
      ServiceType serviceType = ServiceType.SGO;
      String sort = "id,asc";
      String relativeUrl = "/areas-conocimiento?s=" + sort + "&q=" + query;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<AreaConocimientoDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<AreaConocimientoDto>>() {
          }).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

}
