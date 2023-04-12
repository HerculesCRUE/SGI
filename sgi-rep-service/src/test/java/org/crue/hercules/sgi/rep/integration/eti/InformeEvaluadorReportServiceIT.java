package org.crue.hercules.sgi.rep.integration.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.integration.BaseIT;
import org.crue.hercules.sgi.rep.service.eti.InformeEvaluadorReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InformeEvaluadorReportServiceIT extends BaseIT {

  @Autowired
  InformeEvaluadorReportService service;

  @Test
  void testPdfInformeEvaluador() throws Exception {
    // given: data for report
    String reportPath = "rep-eti-ficha-evaluador-prpt";
    String reportName = "reportInformeEvaluador_";
    String outputJson = "eti/informeEvaluador.json";

    // @formatter:off
    SgiReportDto report = SgiReportDto.builder()
      .path(reportPath)
      .name(reportName)
      .outputType(OutputType.PDF)
      .build();

    // @formatter:on

    // when: generate report
    service.getReportInformeEvaluadorEvaluacionFromJson(report, outputJson);

    // given: report generated
    assertNotNull(report);
  }

}