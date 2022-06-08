package org.crue.hercules.sgi.rep.service.sgi;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.BaseReportServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * SgiApiPrcServiceTest
 */
class SgiApiPrcServiceTest extends BaseReportServiceTest {

  private SgiApiPrcService sgiApiPrcService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    sgiApiPrcService = new SgiApiPrcService(restApiProperties, restTemplate);
  }

  @Test
  void getDataReportDetalleGrupo_ko() throws Exception {
    Long grupoId = 1L;
    Integer anio = 2022;
    Assertions.assertThatThrownBy(() -> sgiApiPrcService.getDataReportDetalleGrupo(anio, grupoId))
        .isInstanceOf(GetDataReportException.class);
  }

}
