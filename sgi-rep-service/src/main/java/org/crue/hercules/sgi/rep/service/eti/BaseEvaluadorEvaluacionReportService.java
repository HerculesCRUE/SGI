package org.crue.hercules.sgi.rep.service.eti;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.table.TableModel;

import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ElementOutput;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionEvaluadorReportOutput;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio base de generación de informe de evaluación/evaluador de ética
 */
@Service
@Slf4j
@Validated
public abstract class BaseEvaluadorEvaluacionReportService extends BaseApartadosRespuestasReportService {

  protected static final String BLOQUE_0 = "bloque0";

  protected BaseEvaluadorEvaluacionReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, BloqueService bloqueService,
      ApartadoService apartadoService, SgiFormlyService sgiFormlyService, RespuestaService respuestaService) {

    super(sgiConfigProperties, sgiApiConfService, bloqueService, apartadoService, sgiFormlyService, respuestaService);
  }

  /**
   * Devuelve un informe de evaluador/evaluación a partir de un json de pruebas
   * 
   * @param reportOutput   SgiReport
   * @param outputJsonPath String
   */
  public void getReportInformeEvaluadorEvaluacionFromJson(SgiReportDto reportOutput, String outputJsonPath) {

    Map<String, TableModel> hmTableModel = createMockQueryInformeEvaluacionDataModel(outputJsonPath);

    getReportInformeEvaluadorEvaluacionIntern(reportOutput, hmTableModel);
  }

  /**
   * A partir de un json (ReporOutput) crea un mock de DefaultTableModel para la
   * carga de datos en el report y un xls para la carga directa de datos de prueba
   * desde el report designer
   * 
   * @param outputJsonPath String
   * @return Map<String, TableModel>
   */
  private Map<String, TableModel> createMockQueryInformeEvaluacionDataModel(String outputJsonPath) {
    Map<String, TableModel> hmTableModel = null;

    try (InputStream jsonApartadoInputStream = this.getClass().getClassLoader().getResourceAsStream(outputJsonPath)) {
      try (Scanner scanner = new Scanner(jsonApartadoInputStream, "UTF-8").useDelimiter("\\Z")) {
        String outputJson = scanner.next();

        InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput = Jackson2ObjectMapperBuilder
            .json().build().readValue(outputJson, InformeEvaluacionEvaluadorReportOutput.class);

        hmTableModel = generateTableModelFromReportOutput(informeEvaluacionEvaluadorReportOutput);

        getBloque0(hmTableModel, informeEvaluacionEvaluadorReportOutput.getEvaluacion());

        generateXls(
            informeEvaluacionEvaluadorReportOutput.getEvaluacion().getMemoria().getComite().getFormulario().getNombre(),
            hmTableModel, Arrays.asList(COLUMNS_TABLE_MODEL));
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return hmTableModel;
  }

  /**
   * Devuelve un informe de evaluación
   * 
   * @param reportOutput SgiReport
   * @param idEvaluacion Id de la evaluación
   * @return byte[] Report
   */
  public byte[] getReportInformeEvaluadorEvaluacion(SgiReportDto reportOutput, Long idEvaluacion) {

    InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput = this
        .getInformeEvaluadorEvaluacion(idEvaluacion);

    Map<String, TableModel> hmTableModel = generateTableModelFromReportOutput(informeEvaluacionEvaluadorReportOutput);

    getBloque0(hmTableModel, informeEvaluacionEvaluadorReportOutput.getEvaluacion());

    getReportInformeEvaluadorEvaluacionIntern(reportOutput, hmTableModel);

    return reportOutput.getContent();
  }

  /**
   * Devuelve un informe de evaluación
   * 
   * @param reportOutput SgiReportDto
   * @param hmTableModel Map<String, TableModel>
   */
  private void getReportInformeEvaluadorEvaluacionIntern(SgiReportDto reportOutput,
      Map<String, TableModel> hmTableModel) {

    if (null != hmTableModel && !hmTableModel.isEmpty()) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

        MasterReport report = getReportDefinition(reportOutput.getPath());

        TableDataFactory dataFactory = new TableDataFactory();

        for (Entry<String, TableModel> entryDataModel : hmTableModel.entrySet()) {
          String queryKey = entryDataModel.getKey();
          TableModel tableModel = entryDataModel.getValue();

          parseEntryTableModel(report, dataFactory, queryKey, tableModel);
        }
        report.setDataFactory(dataFactory);

        PdfReportUtil.createPDF(report, baos);

        reportOutput.setContent(baos.toByteArray());

      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new GetDataReportException();
      }
    }
  }

  private void parseEntryTableModel(MasterReport report, TableDataFactory dataFactory, String queryKey,
      TableModel tableModel) {
    String[] queryKeys = queryKey.split(SEPARATOR_KEY);
    String actionType = queryKeys[1];
    String elementType = queryKeys[2];

    switch (actionType) {
      case BLOQUE_0:
        SubReport subReport = (SubReport) report.getReportHeader().getElement(0);
        subReport.setQuery(queryKey);
        TableDataFactory dataFactorySubReport = new TableDataFactory();
        dataFactorySubReport.addTable(queryKey, tableModel);
        subReport.setDataFactory(dataFactorySubReport);
        break;
      case SUBREPORT_TYPE:
        String elementKey = queryKeys[3];
        if (elementType.equals(SgiFormlyService.TABLE_CRUD_TYPE)) {
          generateSubreportTableCrud(report, queryKey, tableModel, elementKey);
        }
        break;
      default:
        dataFactory.addTable(queryKey, tableModel);
    }
  }

  /**
   * Genera un tableModel en función del tipo de elemento que le pasemos
   * 
   * @param hmTableModel Map con el nombre de la query y el TableModel
   * @param elemento     ElementOutput
   */
  protected void parseElementTypeFromTableModel(Map<String, TableModel> hmTableModel, ElementOutput elemento) {
    try {
      if (elemento.getTipo().equals(SgiFormlyService.TABLE_CRUD_TYPE)) {

        generateKeyTableModelFromTableCrud(hmTableModel, elemento);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  /**
   * Obtiene los datos de cabecera del informe
   * 
   * @param hmTableModel Map con el nombre de la query y el TableModel
   * @param evaluacion   EvaluacionDto
   */
  abstract void getBloque0(Map<String, TableModel> hmTableModel, EvaluacionDto evaluacion);

  /**
   * Devuelve un informe pdf del informe de evaluación/evaluador
   *
   * @param idEvaluacion Id de la evaluación
   * @return InformeEvaluacionEvaluadorReportOutput Datos a presentar en el
   *         informe
   */
  abstract InformeEvaluacionEvaluadorReportOutput getInformeEvaluadorEvaluacion(Long idEvaluacion);
}