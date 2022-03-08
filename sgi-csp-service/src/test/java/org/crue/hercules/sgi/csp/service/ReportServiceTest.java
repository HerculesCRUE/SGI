package org.crue.hercules.sgi.csp.service;

import static org.mockito.ArgumentMatchers.anyString;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.service.sgi.SgiApiRepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.internal.matchers.VarargMatcher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class ReportServiceTest extends BaseServiceTest {

  @Mock
  private RestApiProperties restApiProperties;
  @Mock
  private RestTemplate restTemplate;

  private SgiApiRepService reportService;

  @BeforeEach
  public void setup() {
    this.reportService = new SgiApiRepService(restApiProperties, restTemplate);
  }

  @Test
  void getInformeAutorizacion_ReturnsResource() {
    Long idAutorizacion = 1L;
    final ResponseEntity<Resource> expectedResource = ResponseEntity.ok(new ClassPathResource("application.yml"));

    BDDMockito.given(restApiProperties.getRepUrl()).willReturn("http://localhost");

    BDDMockito
        .given(this.restTemplate.exchange(anyString(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(), ArgumentMatchers.<ParameterizedTypeReference<Resource>>any(),
            ArgumentMatchers.<Long>any()))
        .willReturn(expectedResource);

    Resource resource = reportService.getInformeAutorizacion(idAutorizacion);

    Assertions.assertThat(resource).isNotNull().isEqualTo(expectedResource.getBody());
  }
}