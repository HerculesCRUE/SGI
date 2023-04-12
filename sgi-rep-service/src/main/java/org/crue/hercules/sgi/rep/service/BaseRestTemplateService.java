package org.crue.hercules.sgi.rep.service;

import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.dto.BaseRestDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public abstract class BaseRestTemplateService<T extends BaseRestDto> {

  protected static final int MAX_SIZE = 1000000;

  private final String urlBase;
  private final RestTemplate restTemplate;

  protected BaseRestTemplateService(String urlBase, RestTemplate restTemplate) {
    this.urlBase = urlBase;
    this.restTemplate = restTemplate;
  }

  protected RestTemplate getRestTemplate() {
    return restTemplate;
  }

  protected String getUrlBase() {
    return urlBase;
  }

  @SuppressWarnings({ "unchecked" })
  private Class<T> getGenericClass() {
    Class<T> type = null;
    ParameterizedType superClass = null;
    try {
      superClass = (ParameterizedType) getClass().getGenericSuperclass();
    } catch (Exception e1) {
      superClass = (ParameterizedType) getClass().getSuperclass().getGenericSuperclass();
    }
    if (null != superClass) {
      type = (Class<T>) superClass.getActualTypeArguments()[0];
    }
    return type;
  }

  protected abstract String getUrlApi();

  public T findById(Long id) {
    T dto = null;
    try {
      Class<T> genericClass = getGenericClass();
      String url = urlBase + getUrlApi() + "/" + id;
      HttpEntity<T> httpEntity = new HttpEntityBuilder<T>().withCurrentUserAuthorization().build();

      if (null != genericClass && null != id && null != httpEntity && StringUtils.hasText(url)) {
        final ResponseEntity<T> response = restTemplate.exchange(urlBase + getUrlApi() + "/" + id, HttpMethod.GET,
            httpEntity, genericClass);

        dto = response.getBody();
      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return dto;
  }

  public List<T> findAll(String query, Pageable paging, ParameterizedTypeReference<List<T>> responseType) {
    List<T> result = null;
    try {

      Integer size = getPageSize(paging);
      Integer index = getPageNumber(paging);

      String sort = paging.getSort().get()
          .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
          .collect(Collectors.joining(","));

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.add("X-Page", index.toString());
      httpHeaders.add("X-Page-Size", size.toString());

      String endPoint = urlBase + getUrlApi();

      URI uri = UriComponentsBuilder.fromUriString(endPoint).queryParam("s", sort).queryParam("q", query).build(false)
          .toUri();
      result = findAllFromURI(uri, httpHeaders, responseType);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
    return result;
  }

  protected List<T> findAllFromURI(URI uri, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> responseType) {

    HttpEntity<T> httpEntity = new HttpEntityBuilder<T>().withHeaders(httpHeaders).withCurrentUserAuthorization()
        .build();

    return exchangeAsList(uri.toString(), httpEntity, responseType);

  }

  protected List<T> exchangeAsList(String uri, HttpEntity<T> httpEntity,
      ParameterizedTypeReference<List<T>> responseType) {
    return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, responseType).getBody();
  }

  protected T findFromURI(URI uri, HttpHeaders httpHeaders, ParameterizedTypeReference<T> responseType) {

    HttpEntity<T> httpEntity = new HttpEntityBuilder<T>().withHeaders(httpHeaders).withCurrentUserAuthorization()
        .build();

    return exchangeAsObject(uri.toString(), httpEntity, responseType);

  }

  protected T exchangeAsObject(String uri, HttpEntity<T> httpEntity,
      ParameterizedTypeReference<T> responseType) {
    return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, responseType).getBody();
  }

  private Integer getPageNumber(Pageable paging) {
    Integer index = 0;
    try {
      index = paging.getPageNumber();
    } catch (Exception e) {
      log.debug(e.getMessage());
    }
    return index;
  }

  private Integer getPageSize(Pageable paging) {
    Integer size = MAX_SIZE;
    try {
      size = paging.getPageSize();
    } catch (Exception e) {
      log.debug(e.getMessage());
    }
    return size;
  }
}
