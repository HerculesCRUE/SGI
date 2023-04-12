package org.crue.hercules.sgi.rep.service.sgi;

import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.enums.ServiceType;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiConfService extends SgiApiBaseService {

  private static final String PATH_DELIMITER = "/";
  private static final String PATH_NAME = PATH_DELIMITER + "{name}";
  private static final String PATH_RESOURCES = PATH_DELIMITER + "resources";
  private static final String PATH_RESOURCE_BY_NAME = PATH_RESOURCES + PATH_NAME;

  private final ServiceType serviceType;

  public SgiApiConfService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
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
      throw new GetDataReportException();
    }

  }

  public String getServiceBaseURL() {
    return super.getServiceBaseURL(serviceType);
  }

}
