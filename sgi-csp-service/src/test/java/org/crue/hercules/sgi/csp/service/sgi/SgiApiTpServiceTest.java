package org.crue.hercules.sgi.csp.service.sgi;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.tp.SgiApiInstantTaskOutput;
import org.crue.hercules.sgi.csp.enums.ServiceType;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SgiApiTpServiceTest extends BaseServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private RestApiProperties restApiProperties;

  private SgiApiTpService sgiApiTaskService;

  @BeforeEach
  public void setup() {
    BDDMockito.given(restApiProperties.getTpUrl()).willReturn("http://localhost");
    this.sgiApiTaskService = new SgiApiTpService(restApiProperties, restTemplate);
  }

  @Test
  void createInstantTask_ReturnsSgiApiInstantTaskOutput() {

    SgiApiInstantTaskOutput taskOutput = this.buildMockSgiApiInstantTaskOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<SgiApiInstantTaskOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(taskOutput));

    SgiApiInstantTaskOutput result = this.sgiApiTaskService.createInstantTask(ServiceType.CSP, HttpMethod.GET, "/path",
        "testing", Instant.now());

    Assertions.assertThat(result).isNotNull();
  }

  @Test
  void updateInstantTask_ReturnsSgiApiInstantTaskOutput() {
    Long taskId = 1L;

    SgiApiInstantTaskOutput taskOutput = this.buildMockSgiApiInstantTaskOutput(taskId);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<SgiApiInstantTaskOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(taskOutput));

    SgiApiInstantTaskOutput result = this.sgiApiTaskService.updateInstantTask(taskId, ServiceType.CSP, HttpMethod.GET,
        "/path",
        "testing", Instant.now());

    Assertions.assertThat(result).isNotNull();
  }

  @Test
  void deleteTask_ReturnsVoid() {

    this.sgiApiTaskService.deleteTask(1L);

    verify(this.restTemplate, times(1)).exchange(ArgumentMatchers
        .<String>any(), ArgumentMatchers.<HttpMethod>any(),
        ArgumentMatchers.<HttpEntity<Object>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<SgiApiInstantTaskOutput>>any(),
        ArgumentMatchers.<Object>any());

  }

  @Test
  void findInstantTaskById_ReturnsSgiApiInstantTaskOutput() {
    Long taskId = 1L;

    SgiApiInstantTaskOutput taskOutput = this.buildMockSgiApiInstantTaskOutput(taskId);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<SgiApiInstantTaskOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(taskOutput));

    SgiApiInstantTaskOutput result = this.sgiApiTaskService.findInstantTaskById(taskId);

    Assertions.assertThat(result).isNotNull();
  }

  @Test
  void createSendEmailTask_ReturnsLong() {
    SgiApiInstantTaskOutput taskOutput = this.buildMockSgiApiInstantTaskOutput(1L);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<SgiApiInstantTaskOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(taskOutput));

    Long id = this.sgiApiTaskService.createSendEmailTask(2L, Instant.now());

    Assertions.assertThat(id).isEqualTo(taskOutput.getId());
  }

  @Test
  void updateSendEmailTask_ShouldUpdateSendEmailTask() {
    Long taskId = 1L;
    SgiApiInstantTaskOutput taskOutput = this.buildMockSgiApiInstantTaskOutput(taskId);

    BDDMockito
        .given(this.restTemplate.exchange(ArgumentMatchers
            .<String>any(), ArgumentMatchers.<HttpMethod>any(),
            ArgumentMatchers.<HttpEntity<Object>>any(),
            ArgumentMatchers.<ParameterizedTypeReference<SgiApiInstantTaskOutput>>any(),
            ArgumentMatchers.<Object>any()))
        .willReturn(ResponseEntity.ok(taskOutput));

    this.sgiApiTaskService.updateSendEmailTask(taskId, 2L, Instant.now());

    verify(this.restTemplate, times(1)).exchange(ArgumentMatchers
        .<String>any(), ArgumentMatchers.<HttpMethod>any(),
        ArgumentMatchers.<HttpEntity<Object>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<SgiApiInstantTaskOutput>>any(),
        ArgumentMatchers.<Object>any());
  }

  private SgiApiInstantTaskOutput buildMockSgiApiInstantTaskOutput(Long id) {
    return SgiApiInstantTaskOutput.builder()
        .id(id)
        .build();
  }

}