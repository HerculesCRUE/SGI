package org.crue.hercules.sgi.rep.service.sgi;

import java.util.Collections;
import java.util.List;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.cnf.ConfigOutput;
import org.crue.hercules.sgi.rep.enums.ServiceType;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiConfService extends SgiApiBaseService {

  private static final String PATH_DELIMITER = "/";
  private static final String PATH_NAME = PATH_DELIMITER + "{name}";
  private static final String PATH_RESOURCES = PATH_DELIMITER + "resources";
  private static final String PATH_RESOURCE_BY_NAME = PATH_RESOURCES + PATH_NAME;
  private static final String PATH_CONFIG = PATH_DELIMITER + "config";
  private static final String PATH_CONFIG_BY_NAME = PATH_CONFIG + PATH_NAME;

  private final ServiceType serviceType;
  private final ObjectMapper mapper;

  public SgiApiConfService(RestApiProperties restApiProperties, RestTemplate restTemplate, ObjectMapper mapper) {
    super(restApiProperties, restTemplate);
    this.mapper = mapper;
    this.serviceType = ServiceType.CNF;
  }

  /**
   * Devuelve un resource
   *
   * @param name nombre del resource
   * @return el resource
   */
  public byte[] getResource(String name) {
    log.debug("getResource({}) - start", name);
    byte[] resource = null;
    try {
      HttpMethod httpMethod = HttpMethod.GET;
      String mergedURL = buildUri(serviceType, PATH_RESOURCE_BY_NAME);

      resource = super.callEndpoint(mergedURL, httpMethod,
          new ParameterizedTypeReference<byte[]>() {
          }, name).getBody();
      log.debug("getResource({}) - end", name);
      return resource;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }

  }

  /**
   * Retorna un valor de configuración que esté basado en una lista de strings en
   * formato JSON.
   * 
   * @param name
   * @return
   * @throws JsonProcessingException
   */
  public List<String> findStringListByName(String name) throws JsonProcessingException {
    log.debug("findStringListByName(String name) - start");
    ConfigOutput value = null;

    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, PATH_CONFIG_BY_NAME);
    value = super.callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<ConfigOutput>() {
        }, name).getBody();
    log.debug("getResource({}) - end", name);

    if (value != null) {
      return mapper.readValue(value.getValue(),
          TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
    }
    return Collections.emptyList();
  }

  public String getServiceBaseURL() {
    return super.getServiceBaseURL(serviceType);
  }

}
