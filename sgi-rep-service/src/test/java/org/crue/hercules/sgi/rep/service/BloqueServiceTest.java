package org.crue.hercules.sgi.rep.service;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.eti.BloqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * BloqueServiceTest
 */
class BloqueServiceTest extends BaseReportServiceTest {

  private BloqueService bloqueService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    bloqueService = new BloqueService(restApiProperties, restTemplate);
  }

  @Test
  void findByFormularioId_ReturnsException() throws Exception {
    Long idBloque = 1L;

    Assertions.assertThatThrownBy(() -> bloqueService.findByFormularioId(idBloque))
        .isInstanceOf(GetDataReportException.class);
  }

}
