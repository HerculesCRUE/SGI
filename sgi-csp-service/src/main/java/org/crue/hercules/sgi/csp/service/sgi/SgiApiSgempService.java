package org.crue.hercules.sgi.csp.service.sgi;

import lombok.extern.slf4j.Slf4j;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.sgemp.EmpresaOutput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class SgiApiSgempService extends SgiApiBaseService {

  public SgiApiSgempService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  /**
   * Obtiene los datos de una entidad, de SGEMP, a través de su identificador
   * 
   * @param id Identificador de la entidad
   * @return {@link EmpresaOutput}
   */
  public EmpresaOutput findById(String id) {
    log.debug("findById({}) - start", id);

    Assert.notNull(id, "ID is required");

    ServiceType serviceType = ServiceType.SGEMP;
    String relativeUrl = "/empresas/{id}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final EmpresaOutput response = super.<EmpresaOutput>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<EmpresaOutput>() {
        }, id).getBody();

    log.debug("findById({}) - end", id);
    return response;
  }

}
