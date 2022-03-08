package org.crue.hercules.sgi.csp.service.sgi;

import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.csp.config.RestApiProperties;
import org.crue.hercules.sgi.csp.dto.cnf.ConfigOutput;
import org.crue.hercules.sgi.csp.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class SgiApiCnfServiceTest extends BaseServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private RestApiProperties restApiProperties;
  @Mock
  private ObjectMapper mapper;

  private SgiApiCnfService configService;

  @BeforeEach
  public void setup() {
    BDDMockito.given(restApiProperties.getCnfUrl()).willReturn("http://localhost");
    this.configService = new SgiApiCnfService(this.restApiProperties, this.restTemplate, this.mapper);
  }

  // @Test
  void findByName() {
    ConfigOutput configOutput = this.buildMockConfigOutput("test-path", "testing", "testing");

    BDDMockito.given(restTemplate.exchange(
        anyString(), ArgumentMatchers.<HttpMethod>any(),
        ArgumentMatchers.<HttpEntity<Object>>any(),
        ArgumentMatchers.<Class<ConfigOutput>>any())).willReturn(ResponseEntity.ok(configOutput));

    String result = this.configService.findByName("testing");

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result).isEqualTo(configOutput.getValue());
  }

  // @Test
  void findStringListByName() {
  }

  private ConfigOutput buildMockConfigOutput(String name, String description, String value) {
    ConfigOutput configOutput = new ConfigOutput();
    configOutput.setDescription(description);
    configOutput.setName(name);
    configOutput.setValue(value);

    return configOutput;
  }
}