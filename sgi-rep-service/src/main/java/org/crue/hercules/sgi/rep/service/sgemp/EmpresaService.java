package org.crue.hercules.sgi.rep.service.sgemp;

import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmpresaService {
  private static final String URL_API = "/empresas";

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  public EmpresaService(RestApiProperties restApiProperties, RestTemplate restTemplate) {
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
  }

  public EmpresaDto findById(String id) {
    EmpresaDto dto = null;
    try {
      final ResponseEntity<EmpresaDto> response = restTemplate.exchange(
          restApiProperties.getSgempUrl() + URL_API + "/" + id, HttpMethod.GET,
          new HttpEntityBuilder<EmpresaDto>().withCurrentUserAuthorization().build(), EmpresaDto.class);

      dto = response.getBody();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return dto;
  }

}
