package org.crue.hercules.sgi.rep.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * BaseReportServiceTest
 */
public abstract class BaseReportServiceTest extends BaseServiceTest {

  protected SgiDynamicReportDto generarMockSgiDynamicReport(OutputType outputType) {
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
      .outputType(outputType)
      .title("Listado de prueba de informe dinámico")
      .filters(filters)
      .columns(columns)
      .rows(rows)
      .build();
    // @formatter:on
    return report;
  }

  protected SgiReportDto generarMockSgiReport(OutputType outputType) {
    return SgiReportDto.builder()
        .path("report/eti/mxx.prpt")
        .name("informeDinamico")
        .outputType(outputType)
        .dataModel(generarMockDataModel())
        .parameters(generarMockDataReportParameters())
        .build();
  }

  private Map<String, Object> generarMockDataReportParameters() {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("1", "1");
    parameters.put("2", "2");
    return parameters;
  }

  protected Map<String, TableModel> generarMockDataModel() {
    Map<String, TableModel> dataModel = new HashMap<>();
    dataModel.put("Query1", new DefaultTableModel());
    return dataModel;
  }

  protected ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    return objectMapper;
  }

  protected SgiDynamicReportDto getSgiDynamicReportFromJson(String outputJsonPath) throws Exception {
    ObjectMapper objectMapper = getObjectMapper();
    return objectMapper.readValue(getJsonFromResources(outputJsonPath), SgiDynamicReportDto.class);
  }

  protected String getJsonFromResources(String outputJsonPath) throws Exception {
    String jsonValue = null;

    try (InputStream jsonApartadoInputStream = this.getClass().getClassLoader().getResourceAsStream(outputJsonPath)) {
      try (Scanner scanner = new Scanner(jsonApartadoInputStream, "UTF-8").useDelimiter("\\Z")) {
        jsonValue = scanner.next();
      }
    } catch (Exception e) {
      throw e;
    }
    return jsonValue;
  }

  protected byte[] getResource(String path) throws Exception {
    return Files.readAllBytes(Paths.get(this.getClass().getClassLoader().getResource(path).toURI()));
  }

}
