package org.crue.hercules.sgi.rep.service.eti;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * FormularioServiceTest
 */
class FormularioServiceTest extends BaseReportEtiServiceTest {

  private FormularioService respuestaService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    respuestaService = new FormularioService(restApiProperties, restTemplate);
  }

  @Test
  void findByMemoriaId_ReturnsException() throws Exception {
    Assertions.assertThatThrownBy(() -> respuestaService.findByMemoriaId(1L))
        .isInstanceOf(GetDataReportException.class);
  }

}
