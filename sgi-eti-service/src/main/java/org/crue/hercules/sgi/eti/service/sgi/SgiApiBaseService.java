package org.crue.hercules.sgi.eti.service.sgi;

import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.enums.ServiceType;
import org.crue.hercules.sgi.eti.exceptions.UnknownServiceTypeException;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SgiApiBaseService {
  public static final String CLIENT_REGISTRATION_ID = "eti-service";
  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  protected SgiApiBaseService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  protected String buildUri(
      ServiceType serviceType, String relativeUrl) {
    log.debug("buildUrl(ServiceType serviceType, String relativeUrl) - start");
    String serviceURL = null;
    switch (serviceType) {
      case REP:
        serviceURL = restApiProperties.getRepUrl();
        break;
      case SGDOC:
        serviceURL = restApiProperties.getSgdocUrl();
        break;
      case SGP:
        serviceURL = restApiProperties.getSgpUrl();
        break;
      case COM:
        serviceURL = restApiProperties.getComUrl();
        break;
      default:
        throw new UnknownServiceTypeException(serviceType.name());
    }
    // TODO revisit implementation
    String mergedURL = new StringBuilder(serviceURL).append(relativeUrl).toString();
    log.debug("buildUrl(ServiceType serviceType, String relativeUrl) - end");
    return mergedURL;
  }

  protected <T> ResponseEntity<T> callEndpoint(String endPoint, HttpMethod httpMethod,
      ParameterizedTypeReference<T> returnType, Object... uriVariables) {
    return this.<Void, T>callEndpoint(endPoint, httpMethod, null, returnType, uriVariables);
  }

  protected <E, T> ResponseEntity<T> callEndpoint(String endPoint, HttpMethod httpMethod, E entity,
      ParameterizedTypeReference<T> returnType, Object... uriVariables) {
    log.info("Calling SGI API endpoint: {}", endPoint);
    log.debug("Endpoint uri variables: {} \\nRequest data: {}", (Object[]) uriVariables, entity);
    HttpEntity<E> request = new HttpEntityBuilder<E>().withEntity(entity)
        .withClientAuthorization(CLIENT_REGISTRATION_ID).build();

    ResponseEntity<T> response = restTemplate.exchange(endPoint, httpMethod, request,
        returnType, uriVariables);

    log.debug("Endpoint response: {}", response);
    return response;
  }

  /**
   * Call end point with current user authorization
   * 
   * @param <T>          Expected return type
   * @param endPoint     The end point URL
   * @param httpMethod   The HTTP method
   * @param returnType   The parametrized expected return type
   * @param uriVariables Variables to replace in the URI
   * @return The API response
   * 
   * @deprecated Use b2b security
   */
  @Deprecated
  protected <T> ResponseEntity<T> callEndpointWithCurrentUserAuthorization(String endPoint, HttpMethod httpMethod,
      ParameterizedTypeReference<T> returnType, Object... uriVariables) {
    return this.<Void, T>callEndpointWithCurrentUserAuthorization(endPoint, httpMethod, null, returnType, uriVariables);
  }

  /**
   * Call end point with current user authorization
   * 
   * @param <E>          Type of the sent entity
   * @param <T>          Data expected return type
   * @param endPoint     The end point URL
   * @param httpMethod   The HTTP method
   * @param entity       The data to be sent
   * @param returnType   The parametrized expected return type
   * @param uriVariables Variables to replace in the URI
   * @return The API response
   * 
   * @deprecated Use b2b security
   */
  // TODO Use b2b security and delete
  @Deprecated
  protected <E, T> ResponseEntity<T> callEndpointWithCurrentUserAuthorization(String endPoint, HttpMethod httpMethod,
      E entity,
      ParameterizedTypeReference<T> returnType, Object... uriVariables) {
    log.info("Calling SGI API endpoint: {}", endPoint);
    log.debug("Endpoint uri variables: {} \\nRequest data: {}", (Object[]) uriVariables, entity);
    HttpEntity<E> request = new HttpEntityBuilder<E>().withEntity(entity)
        .withCurrentUserAuthorization().build();

    ResponseEntity<T> response = restTemplate.exchange(endPoint, httpMethod, request,
        returnType, uriVariables);

    log.debug("Endpoint response: {}", response);
    return response;
  }
}
