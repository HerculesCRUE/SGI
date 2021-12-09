package org.crue.hercules.sgi.rep.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SgiDynamicReportServiceTest
 */
class SgiDynamicReportServiceTest extends BaseReportServiceTest {

  private SgiDynamicReportService sgiDynamicReportService;

  @Autowired
  private SgiConfigProperties sgiConfigProperties;

  @BeforeEach
  public void setUp() throws Exception {
    sgiDynamicReportService = new SgiDynamicReportService(sgiConfigProperties);
  }

  @ParameterizedTest
  @ValueSource(strings = { "PDF", "RTF", "XLS", "XLSX", "CSV", "HTML" })
  void getDynamicReport_ReturnsResource(String outputType) throws Exception {
    // given: data for report
    SgiDynamicReportDto report = generarMockSgiDynamicReport(OutputType.valueOf(outputType));

    // when: generate report
    sgiDynamicReportService.generateDynamicReport(report);

    // given: report generated
    assertNotNull(report);
  }

  @Test
  void givenDynamicReportDto_ReturnReport() throws Exception {
    // given: data for report

    // @formatter:off
    List<SgiDynamicReportDto.SgiFilterReportDto> filters = new ArrayList<>();
    filters.add(SgiDynamicReportDto.SgiFilterReportDto.builder().name("Filtro1").filter("Valor de filtro 1").build());
    filters.add(SgiDynamicReportDto.SgiFilterReportDto.builder().name("Filtro2").filter("Valor de filtro 2").build());

    List<SgiDynamicReportDto.SgiColumReportDto> columns = new ArrayList<>();
    columns.add(SgiDynamicReportDto.SgiColumReportDto.builder().title("Nombre").name("nombre").type(SgiDynamicReportDto.ColumnType.STRING).build());
    columns.add(SgiDynamicReportDto.SgiColumReportDto.builder().title("Nombre").name("fecha").type(SgiDynamicReportDto.ColumnType.STRING).build());

    List<SgiDynamicReportDto.SgiRowReportDto> rows = new ArrayList<>();
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre1", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre2", "22/10/2021")).build());    
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre3", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre4", "22/10/2021")).build());    
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre5", "12/10/2021")).build());
    rows.add(SgiDynamicReportDto.SgiRowReportDto.builder().elements(Arrays.asList("Nombre6", "22/10/2021")).build());

    SgiDynamicReportDto report = SgiDynamicReportDto.builder()
      .name("informeDinamico")
      .outputType(OutputType.PDF)
      .title("Listado de prueba de informe dinámico")
      .filters(filters)
      .columns(columns)
      .rows(rows)
      .build();
    // @formatter:on

    // when: generate report
    sgiDynamicReportService.generateDynamicReport(report);

    // given: report generated
    assertNotNull(report);
  }

  @ParameterizedTest
  @CsvSource({
      "'dynamic.json', 'PDF'",
      "'dynamic_with_colums_repeated.json', 'PDF'",
      "'dynamic_with_list.json', 'PDF'",
      "'dynamic_with_subreport.json', 'PDF'",
      "'dynamic.json', 'RTF'",
      "'dynamic_with_colums_repeated.json', 'RTF'",
      "'dynamic_with_list.json', 'RTF'",
      "'dynamic_with_subreport.json', 'RTF'",
      "'dynamic.json', 'HTML'",
      "'dynamic_with_colums_repeated.json', 'HTML'",
      "'dynamic_with_list.json', 'HTML'",
      "'dynamic_with_subreport.json', 'HTML'",
      "'dynamic.json', 'XLS'",
      "'dynamic_with_colums_repeated.json', 'XLS'",
      "'dynamic_with_list.json', 'XLS'",
      "'dynamic_with_subreport.json', 'XLS'",
      "'dynamic.json', 'XLSX'",
      "'dynamic_with_colums_repeated.json', 'XLSX'",
      "'dynamic_with_list.json', 'XLSX'",
      "'dynamic_with_subreport.json', 'XLSX'",
      "'dynamic.json', 'CSV'",
      "'dynamic_with_colums_repeated.json', 'CSV'",
      "'dynamic_with_list.json', 'CSV'",
      "'dynamic_with_subreport.json', 'CSV'",
  })
  void givenDynamicReportJson_ReturnReport(String fileName, String outputType) throws Exception {
    // given: data for report
    String outputJsonPath = "common/" + fileName;
    SgiDynamicReportDto report = getSgiDynamicReportFromJson(outputJsonPath);
    report.setOutputType(OutputType.valueOf(outputType));

    // when: generate report
    sgiDynamicReportService.generateDynamicReport(report);

    // given: report generated
    assertNotNull(report);
  }
}
