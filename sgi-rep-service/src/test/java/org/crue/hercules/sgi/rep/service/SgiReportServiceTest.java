package org.crue.hercules.sgi.rep.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SgiReportServiceTest
 */
class SgiReportServiceTest extends BaseReportServiceTest {

  private SgiReportService sgiReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @Mock
  private SgiApiConfService sgiApiConfService;

  @BeforeEach
  public void setUp() throws Exception {
    sgiReportService = new SgiReportService(sgiConfigProperties, sgiApiConfService);
  }

  @ValueSource(strings = { "PDF", "RTF", "XLS", "XLSX", "CSV", "HTML" })
  void getDynamicReport_ReturnsResource(String outputType) throws Exception {
    // given: data for report
    SgiReportDto report = generarMockSgiReport(OutputType.valueOf(outputType));

    BDDMockito.given(sgiApiConfService.getResource(ArgumentMatchers.<String>any()))
        .willReturn(getResource("common/prpt/rep-common-dynamic-landscape.prpt"));

    // when: generate report
    sgiReportService.generateReport(report);

    // given: report generated
    assertNotNull(report);
  }

}
