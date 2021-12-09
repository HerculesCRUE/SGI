package org.crue.hercules.sgi.rep.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.eti.RespuestaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * RespuestaServiceTest
 */
class RespuestaServiceTest extends BaseReportServiceTest {

  private RespuestaService respuestaService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    respuestaService = new RespuestaService(restApiProperties, restTemplate);
  }

  @Test
  void findByMemoriaIdAndApartadoId_ReturnsException() throws Exception {
    Assertions.assertThatThrownBy(() -> respuestaService.findByMemoriaIdAndApartadoId(1L, 1L))
        .isInstanceOf(GetDataReportException.class);
  }

}
