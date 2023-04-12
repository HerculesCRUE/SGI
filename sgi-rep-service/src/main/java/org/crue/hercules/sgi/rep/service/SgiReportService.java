package org.crue.hercules.sgi.rep.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.table.TableModel;
import javax.validation.Valid;
import javax.validation.groups.Default;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.SgiReportDto.FieldOrientation;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DefaultReportEnvironment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.csv.FastCsvExportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelExportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.StreamCSVOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.StreamRTFOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.FlowExcelOutputProcessor;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes
 */
@Service
@Slf4j
@Validated
public class SgiReportService {

  private final SgiConfigProperties sgiConfigProperties;

  private final SgiApiConfService sgiApiConfService;

  protected static final String EMPTY_TYPE = "empty";
  protected static final String COMPONENT_ID = "component_id";
  protected static final String COMPONENT_TYPE = "component_type";
  protected static final String BAND_TYPE = "band";
  protected static final String QUERY_TYPE = "query";
  protected static final String SUBREPORT_TYPE = "subreport";
  protected static final String SEPARATOR_KEY = "_";
  protected static final String NAME_GENERAL_TABLE_MODEL = "general";
  private static final String ISO_LOCAL_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
  public static final Float WIDTH_PORTRAIT = 460f;
  public static final Float WIDTH_LANDSCAPE = 630f;
  public static final Float WIDTH_FIELD_EXCEL_DEFAULT = 150f;
  public static final Float WIDTH_FIELD_DEFAULT = 50f;

  protected static final String DATE_PATTERN_DEFAULT = "dd/MM/yyyy";
  protected static final String NUMBER_PATTERN_DEFAULT = "0.00";

  private static final String PATH_DELIMITER = "/";
  private static final String PATH_RESOURCES = PATH_DELIMITER + "public/resources/";

  public SgiReportService(SgiConfigProperties sgiConfigProperties, SgiApiConfService sgiApiConfService) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.sgiApiConfService = sgiApiConfService;

    // Initialize the reporting engine
    ClassicEngineBoot.getInstance().start();
  }

  protected DataFactory getDataFactory(Map<String, TableModel> dataModel) {

    TableDataFactory dataFactory = new TableDataFactory();

    for (Entry<String, TableModel> entryDataModel : dataModel.entrySet()) {
      dataFactory.addTable(entryDataModel.getKey(), entryDataModel.getValue());
    }
    return dataFactory;
  }

  @Validated({ SgiReportDto.Create.class, Default.class })
  protected void generateReport(@Valid SgiReportDto sgiReport) {
    try {
      MasterReport report = getReportDefinition(sgiReport.getPath());

      if (report == null) {
        throw new IllegalArgumentException();
      }

      final DataFactory dataFactory = getDataFactory(sgiReport.getDataModel());

      if (dataFactory != null) {
        report.setDataFactory(dataFactory);
      }

      final Map<String, Object> reportParameters = sgiReport.getParameters();
      if (null != reportParameters) {
        for (Entry<String, Object> entry : reportParameters.entrySet()) {
          report.getParameterValues().put(entry.getKey(), entry.getValue());
        }
      }

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputType(), report));
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new GetDataReportException();
    }
  }

  protected byte[] generateReportOutput(final OutputType outputType, final MasterReport report) {
    byte[] reportContent = null;
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStream outputStream = new BufferedOutputStream(baos);

      AbstractReportProcessor reportProcessor = null;
      try {

        switch (outputType) {
          case CSV:
            reportProcessor = getCsvReportProcessor(report, outputStream);
            break;
          case XLSX:
            reportProcessor = getExcelOutputProcessor(report, outputStream, true);
            break;
          case XLS:
            reportProcessor = getExcelOutputProcessor(report, outputStream, false);
            break;
          case HTML:
            reportProcessor = getHtmlOutputProcessor(report, outputStream);
            break;
          case RTF:
            reportProcessor = getRtfOutputProcessor(report, outputStream);
            break;
          case PDF:
          default:
            reportProcessor = getPdfOutputProcessor(report, outputStream);
            break;
        }

        reportProcessor.processReport();

        reportContent = baos.toByteArray();

      } finally {
        if (reportProcessor != null) {
          reportProcessor.close();
        }
        baos.close();
        outputStream.close();
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new GetDataReportException();
    }
    return reportContent;
  }

  private AbstractReportProcessor getExcelOutputProcessor(final MasterReport report, OutputStream outputStream,
      boolean useXlsxFormat) throws ReportProcessingException {
    AbstractReportProcessor reportProcessor = null;

    ReportStructureValidator validator = new ReportStructureValidator();

    if (!validator.isValidForFastProcessing(report)) {
      final FlowExcelOutputProcessor target = new FlowExcelOutputProcessor(report.getConfiguration(), outputStream,
          report.getResourceManager());
      target.setUseXlsxFormat(useXlsxFormat);
      reportProcessor = new FlowReportProcessor(report, target);
    } else {
      reportProcessor = new FastExcelExportProcessor(report, outputStream, true);
    }
    return reportProcessor;

  }

  private AbstractReportProcessor getHtmlOutputProcessor(final MasterReport report, OutputStream outputStream)
      throws ReportProcessingException {
    AbstractReportProcessor reportProcessor = null;
    final StreamRepository targetRepository = new StreamRepository(outputStream);
    final ContentLocation targetRoot = targetRepository.getRoot();
    final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor(report.getConfiguration());
    final HtmlPrinter printer = new AllItemsHtmlPrinter(report.getResourceManager());
    printer.setContentWriter(targetRoot, new DefaultNameGenerator(targetRoot, "index", "html"));
    printer.setDataWriter(null, null);
    printer.setUrlRewriter(new FileSystemURLRewriter());
    outputProcessor.setPrinter(printer);
    reportProcessor = new StreamReportProcessor(report, outputProcessor);
    return reportProcessor;
  }

  private AbstractReportProcessor getRtfOutputProcessor(final MasterReport report, OutputStream outputStream)
      throws ReportProcessingException {
    AbstractReportProcessor reportProcessor;
    StreamRTFOutputProcessor streamRTFOutputProcessor = new StreamRTFOutputProcessor(report.getConfiguration(),
        outputStream, report.getResourceManager());
    reportProcessor = new StreamReportProcessor(report, streamRTFOutputProcessor);
    return reportProcessor;
  }

  private AbstractReportProcessor getPdfOutputProcessor(final MasterReport report, OutputStream outputStream)
      throws ReportProcessingException {
    AbstractReportProcessor reportProcessor;
    final PdfOutputProcessor outputProcessor = new PdfOutputProcessor(report.getConfiguration(), outputStream,
        report.getResourceManager());
    reportProcessor = new PageableReportProcessor(report, outputProcessor);
    return reportProcessor;
  }

  private AbstractReportProcessor getCsvReportProcessor(final MasterReport report, OutputStream outputStream)
      throws ReportProcessingException {
    ReportStructureValidator validator = new ReportStructureValidator();
    AbstractReportProcessor reportProcessor;
    if (!validator.isValidForFastProcessing(report)) {
      StreamCSVOutputProcessor streamCSVOutputProcessor = new StreamCSVOutputProcessor(outputStream);
      reportProcessor = new StreamReportProcessor(report, streamCSVOutputProcessor);
    } else {
      reportProcessor = new FastCsvExportProcessor(report, outputStream);
    }
    return reportProcessor;
  }

  /**
   * Obtiene el report
   * 
   * @param reportPath ruta del report
   * @return MasterReport
   */
  protected MasterReport getReportDefinition(String reportPath) {
    try {
      // Parse the report file
      final ResourceManager resourceManager = new ResourceManager();
      resourceManager.registerDefaults();

      byte[] reportDefinition = sgiApiConfService.getResource(reportPath);
      Resource directly = resourceManager.createDirectly(reportDefinition, MasterReport.class);

      MasterReport report = (MasterReport) directly.getResource();

      DefaultReportEnvironment reportEnvironment = new DefaultReportEnvironment(
          ClassicEngineBoot.getInstance().getGlobalConfig());
      reportEnvironment.setLocale(LocaleContextHolder.getLocale());

      report.setReportEnvironment(reportEnvironment);

      return report;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

  protected void generateXls(String fileNameXls, Map<String, TableModel> hmTableModel, List<String> columnsTableModel) {

    int indexRow = 0;

    try (Workbook wb = new HSSFWorkbook()) {
      Sheet sheet = wb.createSheet("DatosCargaInforme");
      indexRow = generateHeaderRowXls(columnsTableModel, indexRow, sheet);

      if (null != hmTableModel && !hmTableModel.isEmpty()) {
        for (Entry<String, TableModel> entryDataModel : hmTableModel.entrySet()) {
          String queryKey = entryDataModel.getKey();
          if (queryKey.contains(NAME_GENERAL_TABLE_MODEL)) {
            indexRow = generateXlsRowFromTableModel(indexRow, sheet, entryDataModel);
          }
        }
      }

      generateXlsFile(fileNameXls, wb);
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new GetDataReportException();
    }
  }

  private int generateXlsRowFromTableModel(int indexRow, Sheet sheet, Entry<String, TableModel> entryDataModel) {
    TableModel tableModel = entryDataModel.getValue();
    for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
      Row dataRow = sheet.createRow(indexRow++);
      for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
        Object contentCell = tableModel.getValueAt(rowIndex, columnIndex);
        dataRow.createCell(columnIndex).setCellValue(null != contentCell ? contentCell.toString() : "");
      }
    }
    return indexRow;
  }

  private int generateHeaderRowXls(List<String> columnsTableModel, int indexRow, Sheet sheet) {
    Row headerRow = sheet.createRow(indexRow++);
    for (int i = 0; i < columnsTableModel.size(); i++) {
      headerRow.createCell(i).setCellValue(columnsTableModel.get(i));
    }
    return indexRow;
  }

  private void generateXlsFile(String fileNameXls, Workbook wb) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      wb.write(baos);
      File tableModelExcelFile = File.createTempFile(fileNameXls, ".xls");
      FileUtils.writeByteArrayToFile(tableModelExcelFile, baos.toByteArray());
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new GetDataReportException();
    }
  }

  protected String formatInstantToString(Instant instantDate, String pattern) {
    String result = "";

    if (null != instantDate) {
      pattern = StringUtils.hasText(pattern) ? pattern : DATE_PATTERN_DEFAULT;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
          .withZone(sgiConfigProperties.getTimeZone().toZoneId()).withLocale(LocaleContextHolder.getLocale());

      result = formatter.format(instantDate);
    }

    return result;
  }

  protected String formatInstantToString(Instant instantDate) {
    return formatInstantToString(instantDate, DATE_PATTERN_DEFAULT);
  }

  protected Date formatInstantToDate(Instant instantDate) {
    Date result = null;
    try {
      String strFechaWithTimeZone = formatInstantToString(instantDate, ISO_LOCAL_DATE_PATTERN);
      SimpleDateFormat formatter = new SimpleDateFormat(ISO_LOCAL_DATE_PATTERN);
      result = formatter.parse(strFechaWithTimeZone);
    } catch (ParseException e) {
      log.error(e.getMessage());
    }

    return result;
  }

  protected String formatNumberString(String numberString, String pattern) {
    String result = "";

    if (StringUtils.hasText(numberString) && NumberUtils.isParsable(numberString)) {
      pattern = StringUtils.hasText(pattern) ? pattern : NUMBER_PATTERN_DEFAULT;
      DecimalFormat decimalFormat = new DecimalFormat(pattern,
          DecimalFormatSymbols.getInstance(LocaleContextHolder.getLocale()));
      result = decimalFormat.format(Double.parseDouble(numberString));
    }

    return result;
  }

  protected String formatNumberString(String numberString) {
    return formatNumberString(numberString, NUMBER_PATTERN_DEFAULT);
  }

  protected void setCustomWidthSubReport(SgiReportDto reportDto, Integer numColumns) {
    initConfigurationSubReport(reportDto);

    if ((reportDto.getOutputType().equals(OutputType.PDF) || reportDto.getOutputType().equals(OutputType.RTF)
        || reportDto.getOutputType().equals(OutputType.HTML)) && null != numColumns && numColumns.compareTo(0) > 0) {

      float customFieldWidth = reportDto.getCustomWidth() / numColumns;
      reportDto.setColumnMinWidth(
          customFieldWidth > reportDto.getColumnMinWidth() ? customFieldWidth : reportDto.getColumnMinWidth());
    }
  }

  protected void initConfigurationSubReport(SgiReportDto reportDto) {

    if (null == reportDto.getOutputType()) {
      reportDto.setOutputType(OutputType.PDF);
    }

    if (null == reportDto.getFieldOrientation()) {
      reportDto.setFieldOrientation(FieldOrientation.HORIZONTAL);
    }

    if (null == reportDto.getColumnMinWidth()) {
      if (reportDto.getOutputType().equals(OutputType.XLS) || reportDto.getOutputType().equals(OutputType.XLSX)) {

        reportDto.setColumnMinWidth(WIDTH_FIELD_EXCEL_DEFAULT);
      } else {
        reportDto.setColumnMinWidth(WIDTH_FIELD_DEFAULT);

      }
    }

    if (reportDto.getOutputType().equals(OutputType.PDF) || reportDto.getOutputType().equals(OutputType.RTF)) {
      if (null == reportDto.getCustomWidth()) {
        reportDto.setCustomWidth(WIDTH_PORTRAIT);
      }
      if (null == reportDto.getCustomWidth()) {
        reportDto.setColumnMinWidth(WIDTH_PORTRAIT);
      }
    }
  }

  /**
   * Generic message to show into report
   * 
   * @param e Exception
   * @return message encoded in html
   */
  protected String getErrorMessageToReport(Exception e) {
    log.error(e.getMessage());
    return "<b>" + e.getMessage() + "</b>";
  }

  /**
   * URL en la que se encuentran los recursos a los que se puede hacer referencia
   * desde de las plantillas de los informes (ej. imagenes)
   * 
   * @return la URL en la que se encuentran los recursos para los informes
   */
  protected String getRepResourcesBaseURL() {
    return sgiApiConfService.getServiceBaseURL().concat(PATH_RESOURCES);
  }

}