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
 * SgiApiSgePiiServiceTest
 * 
 */
class SgiApiSgePiiServiceTest extends BaseServiceTest {

  private SgiApiSgePiiService sgiApiSgePiiService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    sgiApiSgePiiService = new SgiApiSgePiiService(restApiProperties, restTemplate);
  }

  @Test
  void findIngresosInvencion_ko() throws Exception {
    String query = "id==1";
    Assertions.assertThatThrownBy(() -> sgiApiSgePiiService.findIngresosInvencion(query))
        .isInstanceOf(MicroserviceCallException.class);
  }

  @Test
  void findIngresosInvencionColumnasDef_ko() throws Exception {
    String query = "id==1";
    Assertions.assertThatThrownBy(() -> sgiApiSgePiiService.findIngresosInvencionColumnasDef(query))
        .isInstanceOf(MicroserviceCallException.class);
  }

}
