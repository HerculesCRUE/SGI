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
 * SgiApiPiiServiceTest
 */
class SgiApiPiiServiceTest extends BaseServiceTest {

  private SgiApiPiiService sgiApiPiiService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    sgiApiPiiService = new SgiApiPiiService(restApiProperties, restTemplate);
  }

  @Test
  void findInvencionesProduccionCientifica_ko() throws Exception {
    Integer anioInicio = 2019;
    Integer anioFin = 2021;
    String universidadId = "W23434543";
    Assertions.assertThatThrownBy(() -> sgiApiPiiService.findInvencionesProduccionCientifica(
        anioInicio, anioFin, universidadId)).isInstanceOf(MicroserviceCallException.class);
  }

}
