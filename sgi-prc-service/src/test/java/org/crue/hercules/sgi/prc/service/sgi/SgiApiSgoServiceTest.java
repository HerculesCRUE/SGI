package org.crue.hercules.sgi.prc.service.sgi;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.prc.config.RestApiProperties;
import org.crue.hercules.sgi.prc.exceptions.MicroserviceCallException;
import org.crue.hercules.sgi.prc.service.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * SgiApiSgoServiceTest
 * 
 */
class SgiApiSgoServiceTest extends BaseServiceTest {

  private SgiApiSgoService sgiApiSgoService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    sgiApiSgoService = new SgiApiSgoService(restApiProperties, restTemplate);
  }

  @Test
  void findAll_ReturnsException() throws Exception {
    String query = "id==1";
    Assertions.assertThatThrownBy(() -> sgiApiSgoService.findAllAreasConocimiento(query))
        .isInstanceOf(MicroserviceCallException.class);
  }

}
