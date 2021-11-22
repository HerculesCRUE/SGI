package org.crue.hercules.sgi.tp.tasks;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.tp.config.RestApiProperties;
import org.crue.hercules.sgi.tp.enums.ServiceType;
import org.crue.hercules.sgi.tp.exceptions.UnknownServiceExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SgiApiCallerTask {
  public static final String CLIENT_REGISTRATION_ID = "tp-service";
  private final RestTemplate restTemplate;
  private final RestApiProperties restApiProperties;

  public SgiApiCallerTask(RestTemplate restTemplate, RestApiProperties restApiProperties) {
    this.restTemplate = restTemplate;
    this.restApiProperties = restApiProperties;
  }

  public String call(String serviceType, String relativeUrl, String httpMethod)
      throws URISyntaxException, MalformedURLException {
    log.info("Calling SGI API Service: {} {} {}", httpMethod, serviceType, relativeUrl);
    HttpMethod method = HttpMethod.resolve(httpMethod);
    if (method == null) {
      method = HttpMethod.PATCH;
    }
    String serviceURL = null;
    switch (ServiceType.valueOf(serviceType)) {
    case COM:
      serviceURL = restApiProperties.getComUrl();
      break;
    case CSP:
      serviceURL = restApiProperties.getCspUrl();
      break;
    case ETI:
      serviceURL = restApiProperties.getEtiUrl();
      break;
    case PII:
      serviceURL = restApiProperties.getPiiUrl();
      break;
    case REL:
      serviceURL = restApiProperties.getRelUrl();
      break;
    case REP:
      serviceURL = restApiProperties.getRepUrl();
      break;
    case USR:
      serviceURL = restApiProperties.getUsrUrl();
      break;
    default:
      throw new UnknownServiceExtension(serviceType);
    }
    URL mergedURL = new URL(new URL(serviceURL), relativeUrl);
    String result = call(mergedURL, method);
    log.info("SGI API Service response: {}", result);
    return result;
  }

  private String call(URL url, HttpMethod httpMethod) {
    HttpEntity<?> request = new HttpEntityBuilder<>().withClientAuthorization(CLIENT_REGISTRATION_ID).build();

    ResponseEntity<String> response = restTemplate.exchange(url.toString(), httpMethod, request, String.class);

    return response.getBody();
  }
}
