package org.crue.hercules.sgi.com.service.sgi;

import javax.activation.DataSource;

import org.crue.hercules.sgi.com.config.RestApiProperties;
import org.crue.hercules.sgi.com.dto.sgdoc.Document;
import org.crue.hercules.sgi.com.enums.ServiceType;
import org.crue.hercules.sgi.com.model.DocumentDataSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiSgdocService extends SgiApiBaseService {

  protected SgiApiSgdocService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    super(restApiProperties, restTemplate);
  }

  public DataSource call(String documentRef) {
    log.debug("call(String documentRef) - start");
    ServiceType serviceType = ServiceType.SGDOC;
    String relativeUrl = "/documentos/{documentRef}";
    HttpMethod httpMethod = HttpMethod.GET;
    String mergedURL = buildUri(serviceType, relativeUrl);

    final Document document = super.<Document>callEndpoint(mergedURL, httpMethod,
        new ParameterizedTypeReference<Document>() {
        }, documentRef).getBody();

    String resourceRelativeUrl = relativeUrl + "/archivo";
    String resourceMergedURL = buildUri(ServiceType.SGDOC, resourceRelativeUrl);

    final Resource resource = super.<Resource>callEndpoint(resourceMergedURL, httpMethod,
        new ParameterizedTypeReference<Resource>() {
        }, documentRef).getBody();

    DataSource returnValue = new DocumentDataSource(document, resource);
    log.debug("call(String documentRef) - end");
    return returnValue;
  }
}
