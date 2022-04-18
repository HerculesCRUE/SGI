package org.crue.hercules.sgi.prc.service.sgi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.dto.sgepii.IngresoColumnaDefDto;
import org.crue.hercules.sgi.prc.dto.sgepii.IngresoDto;
import org.crue.hercules.sgi.prc.enums.ServiceType;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiSgePiiService extends SgiApiBaseService {

  public static final String CLIENT_REGISTRATION_ID = "sgepii-service";

  public SgiApiSgePiiService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public List<IngresoDto> findIngresosInvencion(String query) {
    List<IngresoDto> result = new ArrayList<>();

    try {
      ServiceType serviceType = ServiceType.SGEPII;
      String relativeUrl = "/ingresos-invencion?q=" + query;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<IngresoDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<IngresoDto>>() {
          }).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

  public List<IngresoColumnaDefDto> findIngresosInvencionColumnasDef(String query) {
    List<IngresoColumnaDefDto> result = new ArrayList<>();

    try {
      ServiceType serviceType = ServiceType.SGEPII;
      String relativeUrl = "/ingresos-invencion/columnas?q=" + query;
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, relativeUrl);

      result = super.<List<IngresoColumnaDefDto>>callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<List<IngresoColumnaDefDto>>() {
          }).getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MicroserviceCallException();
    }
    return ObjectUtils.defaultIfNull(result, new ArrayList<>());
  }

}
