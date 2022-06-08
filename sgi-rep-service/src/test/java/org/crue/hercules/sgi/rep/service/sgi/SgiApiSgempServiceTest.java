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
 * SgiApiSgempServiceTest
 */
class SgiApiSgempServiceTest extends BaseReportServiceTest {

  private SgiApiSgempService empresaService;

  @Mock
  private RestApiProperties restApiProperties;

  @Mock
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() throws Exception {
    empresaService = new SgiApiSgempService(restApiProperties, restTemplate);
  }

  @Test
  void findById_ReturnsException() throws Exception {
    String empresaRef = "23242342";
    Assertions.assertThatThrownBy(() -> empresaService.findById(empresaRef))
        .isInstanceOf(GetDataReportException.class);
  }
}
