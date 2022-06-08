package org.crue.hercules.sgi.rep.service.eti;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.rep.config.RestApiProperties;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.client.RestTemplate;

/**
 * ConvocatoriaReunionServiceTest
 */
class ConvocatoriaReunionServiceTest extends BaseReportEtiServiceTest {

  private ConvocatoriaReunionService convocatoriaReunionService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    convocatoriaReunionService = new ConvocatoriaReunionService(restApiProperties, restTemplate);
  }

  @Test
  void findAsistentesByConvocatoriaReunionId_ReturnsException() throws Exception {
    Long idConvocatoriaReunion = 1L;

    Assertions
        .assertThatThrownBy(
            () -> convocatoriaReunionService.findAsistentesByConvocatoriaReunionId(idConvocatoriaReunion))
        .isInstanceOf(GetDataReportException.class);
  }

  @Test
  void findConvocatoriaUltimaEvaluacionTipoMemoria_ReturnsException() throws Exception {
    Long idConvocatoriaReunion = 1L;

    Assertions
        .assertThatThrownBy(
            () -> convocatoriaReunionService.findConvocatoriaUltimaEvaluacionTipoMemoria(idConvocatoriaReunion, 1L))
        .isInstanceOf(GetDataReportException.class);
  }

}
