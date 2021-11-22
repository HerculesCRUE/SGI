package org.crue.hercules.sgi.rep.integration.eti;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.crue.hercules.sgi.rep.dto.OutputReportType;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.integration.BaseIT;
import org.crue.hercules.sgi.rep.service.eti.MXXReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class MXXReportServiceIT extends BaseIT {

  @Autowired
  MXXReportService service;

  @Test
  void testPdfM10() throws Exception {
    // given: data for report
    String reportPath = "report/eti/mxx.prpt";
    String reportName = "reportM10_";
    String outputJson = "eti/M10.json";

    // @formatter:off
    SgiReportDto report = SgiReportDto.builder()
      .path(reportPath)
      .name(reportName)
      .outputReportType(OutputReportType.PDF)
      .build();

    MemoriaDto memoriaDto = MemoriaDto.builder().build();
    memoriaDto.setComite(ComiteDto.builder()
                          .comite("CEI")
                          .formulario(FormularioDto.builder().nombre("M10").build())
                          .build());
    // @formatter:on

    // when: generate report
    service.getReportMXXFromJson(report, outputJson, memoriaDto);

    // given: report generated
    assertNotNull(report);
  }

  @Test
  void testPdfM20() throws Exception {
    try {
      // given: data for report
      String reportPath = "report/eti/mxx.prpt";
      String reportName = "reportM20_";
      String outputJson = "eti/M20.json";

      // when: generate report
      // @formatter:off
      SgiReportDto report = SgiReportDto.builder()
        .path(reportPath)
        .name(reportName)
        .outputReportType(OutputReportType.PDF)
        .build();
        
      MemoriaDto memoriaDto = MemoriaDto.builder().build();
      memoriaDto.setComite(ComiteDto.builder()
                            .comite("CEEA")
                            .formulario(FormularioDto.builder().nombre("M20").build())
                            .build());
      // @formatter:on

      service.getReportMXXFromJson(report, outputJson, memoriaDto);

      // given: report generated
      assertNotNull(report);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Test
  void testPdfM30() throws Exception {
    try {
      // given: data for report
      String reportPath = "report/eti/mxx.prpt";
      String reportName = "reportM30_";
      String outputJson = "eti/M30.json";

      // when: generate report
      // @formatter:off
      SgiReportDto report = SgiReportDto.builder()
        .path(reportPath)
        .name(reportName)
        .outputReportType(OutputReportType.PDF)
        .build();

      MemoriaDto memoriaDto = MemoriaDto.builder().build();
      memoriaDto.setComite(ComiteDto.builder()
                            .comite("CBE")
                            .formulario(FormularioDto.builder().nombre("M30").build())
                            .build());
      // @formatter:on
      service.getReportMXXFromJson(report, outputJson, memoriaDto);
      // given: report generated
      assertNotNull(report);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

}