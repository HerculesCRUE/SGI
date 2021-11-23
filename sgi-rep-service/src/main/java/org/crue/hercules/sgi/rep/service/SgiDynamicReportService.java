package org.crue.hercules.sgi.rep.service;

import java.awt.Color;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputReportType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiColumReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiGroupReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiRowReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.TypeColumnReportEnum;
import org.crue.hercules.sgi.rep.dto.SgiReportDto.FieldOrientationType;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.DateFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes dinámicos
 */
@Service
@Slf4j
public class SgiDynamicReportService extends SgiReportService {
  private static final float DYNAMIC_HEIGHT = -100f;
  private static final String NAME_QUERY_INNER = "inner";
  private static final String VALUE_FIELD_SUBREPORT = "value";
  private static final String LABEL_FIELD_SUBREPORT = "label";
  private static final String TITLE_FIELD = "title";
  protected static final String NAME_DETAILS_TABLE_MODEL = "details";
  protected static final String NAME_FILTERS_TABLE_MODEL = "filter";
  protected static final String NAME_SUBREPORT_FILTER = "sub-report-filter";
  protected static final String NAME_GROUP = "group_";

  public SgiDynamicReportService(SgiConfigProperties sgiConfigProperties) {
    super(sgiConfigProperties);
  }

  public void generateDynamicReport(@Valid SgiDynamicReportDto sgiReport) {
    log.debug("generateDynamicReport(sgiReport) - start");

    try {

      setReportConfiguration(sgiReport);

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      setTableModelGeneral(sgiReport, report);

      if (null != sgiReport.getFilters()) {
        setTableModelFilters(sgiReport, report);
      }

      setTableModelDetails(sgiReport, report);

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputReportType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

  protected void setReportConfiguration(SgiDynamicReportDto sgiReport) {

    if (null == sgiReport.getFieldOrientationType()) {
      sgiReport.setFieldOrientationType(FieldOrientationType.HORIZONTAL);
    }

    if (null == sgiReport.getCustomWidth()) {
      sgiReport.setCustomWidth(WIDTH_PORTRAIT);
    }

    if (sgiReport.getCustomWidth().compareTo(WIDTH_PORTRAIT) > 0) {
      if (sgiReport.getOutputReportType().equals(OutputReportType.PDF)) {
        sgiReport.setCustomWidth(WIDTH_LANDSCAPE);
      }
      sgiReport.setPath("report/common/dynamicLandscape.prpt");
    } else {
      sgiReport.setPath("report/common/dynamic.prpt");
    }

    if (!StringUtils.hasText(sgiReport.getName())) {
      sgiReport.setName("dynamic");
    }
  }

  protected void setTableModelDetails(SgiDynamicReportDto sgiReport, final MasterReport report) {
    if (sgiReport.getFieldOrientationType().equals(FieldOrientationType.VERTICAL)) {
      getDynamicSubReportTableCrudVertical(sgiReport, report);
    } else {
      getDynamicSubReportTableCrudHorizontal(sgiReport, report);
    }
  }

  @SuppressWarnings("unchecked")
  protected void getDynamicSubReportTableCrudVertical(SgiDynamicReportDto sgiReport, final MasterReport report) {
    String queryDetails = QUERY_TYPE + SEPARATOR_KEY + NAME_DETAILS_TABLE_MODEL;
    DefaultTableModel tableModelDetails = getTableModelDynamic(sgiReport);

    Vector<Object> columnsData = new Vector<>();
    sgiReport.getColumns().forEach(column -> columnsData.add(column.getName()));

    for (int rowIndex = 0; rowIndex < tableModelDetails.getRowCount(); rowIndex++) {
      Map<String, TableModel> hmTableModel = new HashMap<>();
      String tableModelName = NAME_GENERAL_TABLE_MODEL;

      Vector<Vector<Object>> rowsData = new Vector<>();
      rowsData.add((Vector<Object>) tableModelDetails.getDataVector().get(rowIndex));

      DefaultTableModel tableModelDetailInner = new DefaultTableModel(rowsData, columnsData);
      hmTableModel.put(tableModelName, tableModelDetailInner);
      sgiReport.setDataModel(hmTableModel);
      SubReport subReportTableCrud = getDynamicSubReportTableCrud(sgiReport, queryDetails, tableModelName);

      report.getItemBand().addSubReport(subReportTableCrud);
    }
  }

  protected void getDynamicSubReportTableCrudHorizontal(SgiDynamicReportDto sgiReport, final MasterReport report) {
    String queryDetails = QUERY_TYPE + SEPARATOR_KEY + NAME_DETAILS_TABLE_MODEL;
    DefaultTableModel tableModelDetails = getTableModelDynamic(sgiReport);

    Map<String, TableModel> hmTableModel = new HashMap<>();
    hmTableModel.put(NAME_GENERAL_TABLE_MODEL, tableModelDetails);
    sgiReport.setDataModel(hmTableModel);
    SubReport subReportTableCrud = getDynamicSubReportTableCrud(sgiReport, queryDetails, NAME_GENERAL_TABLE_MODEL);

    report.getItemBand().addSubReport(subReportTableCrud);
  }

  private SubReport getDynamicSubReportTableCrud(SgiDynamicReportDto sgiReport, String queryDetails,
      String tableModelName) {
    SubReport subReportTableCrud = getSubreportTableCrud(sgiReport);
    setDataFactoryToSubReport(subReportTableCrud, sgiReport.getDataModel().get(tableModelName), queryDetails);

    if (null != sgiReport.getGroupBy() && StringUtils.hasText(sgiReport.getGroupBy().getName())
        && sgiReport.getFieldOrientationType().equals(FieldOrientationType.VERTICAL)) {
      addGroup((RelationalGroup) subReportTableCrud.getGroup(0), sgiReport);
    }
    return subReportTableCrud;
  }

  protected void addGroup(RelationalGroup group, SgiDynamicReportDto sgiReport) {
    SgiGroupReportDto groupReportDto = sgiReport.getGroupBy();
    group.setFields(Arrays.asList(groupReportDto.getName()));
    group.setName(NAME_GROUP + groupReportDto.getName());

    if (groupReportDto.getVisible().equals(Boolean.TRUE)) {
      TextFieldElementFactory textFactoryProduct = new TextFieldElementFactory();
      textFactoryProduct.setFieldname(groupReportDto.getName());
      textFactoryProduct.setX(0f);
      textFactoryProduct.setY(0f);
      textFactoryProduct.setMinimumWidth(sgiReport.getCustomWidth());
      textFactoryProduct.setMinimumHeight(DYNAMIC_HEIGHT);
      textFactoryProduct.setVerticalAlignment(ElementAlignment.MIDDLE);
      textFactoryProduct.setPaddingLeft(4f);
      textFactoryProduct.setPaddingRight(4f);
      textFactoryProduct.setPaddingTop(4f);
      textFactoryProduct.setPaddingBottom(4f);
      textFactoryProduct.setDynamicHeight(Boolean.TRUE);
      textFactoryProduct.setWrapText(Boolean.TRUE);
      textFactoryProduct.setNullString("");
      textFactoryProduct.setBold(Boolean.TRUE);
      textFactoryProduct.setBackgroundColor(Color.GRAY);
      group.getHeader().addElement(textFactoryProduct.createElement());
    }
  }

  protected void setTableModelFilters(SgiDynamicReportDto sgiReport, final MasterReport report) {
    String queryFilters = QUERY_TYPE + SEPARATOR_KEY + NAME_FILTERS_TABLE_MODEL;
    DefaultTableModel tableModelFilters = getTableModelFilters(sgiReport);

    TableDataFactory dataFactoryFilters = new TableDataFactory();
    dataFactoryFilters.addTable(queryFilters, tableModelFilters);

    SubReport subreportFilters = Stream.of(report.getReportHeader().getSubReports())
        .filter(sr -> sr.getId().equals(NAME_SUBREPORT_FILTER)).findFirst().orElseThrow(GetDataReportException::new);

    subreportFilters.setDataFactory(dataFactoryFilters);
  }

  protected void setTableModelGeneral(SgiDynamicReportDto sgiReport, final MasterReport report) {
    String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL;
    DefaultTableModel tableModelGeneral = getTableModelForLabel(sgiReport.getTitle());

    TableDataFactory dataFactoryGeneral = new TableDataFactory();
    dataFactoryGeneral.addTable(queryGeneral, tableModelGeneral);
    report.setDataFactory(dataFactoryGeneral);
  }

  protected DefaultTableModel getTableModelForLabel(String label) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsData.add(TITLE_FIELD);
    elementsRow.add(label);

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  protected DefaultTableModel getTableModelFilters(SgiDynamicReportDto sgiReport) {
    Vector<Object> columnsData = getColumsTableModelInnerSubReport();

    Vector<Vector<Object>> rowsData = new Vector<>();
    sgiReport.getFilters().forEach(filter -> {

      Vector<Object> elementsRow = new Vector<>();
      elementsRow.add(filter.getName());
      elementsRow.add(filter.getFilter());

      rowsData.add(elementsRow);
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  protected DefaultTableModel getTableModelDynamic(SgiDynamicReportDto sgiReport) {
    Vector<Object> columnsData = new Vector<>();
    sgiReport.getColumns().forEach(column -> columnsData.add(column.getName()));

    Vector<Vector<Object>> rowsData = new Vector<>();
    sgiReport.getRows().forEach(row -> {

      SgiDynamicReportDto rowSgiDynamicReportDto = SgiDynamicReportDto.builder()
          .outputReportType(sgiReport.getOutputReportType()).fieldOrientationType(sgiReport.getFieldOrientationType())
          .columns(sgiReport.getColumns()).rows(Arrays.asList(row)).build();
      Vector<Object> elementsRow = getRowsTableModelDynamic(rowSgiDynamicReportDto);

      rowsData.add(elementsRow);
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private Vector<Object> getRowsTableModelDynamic(SgiDynamicReportDto reportDto) {
    Vector<Object> elementsRow = new Vector<>();
    SgiRowReportDto row = reportDto.getRows().get(0);
    for (int columnIndex = 0; columnIndex < row.getElements().size(); columnIndex++) {
      SgiColumReportDto sgiColumReportDto = reportDto.getColumns().get(columnIndex);
      switch (sgiColumReportDto.getType()) {

      case DATE:
        if (StringUtils.hasText((String) row.getElements().get(columnIndex))) {
          Instant fechaInstant = Instant.parse((String) row.getElements().get(columnIndex));
          if ((reportDto.getOutputReportType().equals(OutputReportType.XLS)
              || reportDto.getOutputReportType().equals(OutputReportType.XLSX))
              && (reportDto.getFieldOrientationType().equals(FieldOrientationType.HORIZONTAL))) {
            elementsRow.add(formatInstantToDate(fechaInstant));
          } else {
            elementsRow.add(formatInstantToString(fechaInstant, sgiColumReportDto.getFormat()));
          }
        } else {
          elementsRow.add("");
        }
        break;
      case SUBREPORT:
        SgiDynamicReportDto rowSgiDynamicReportDto = SgiDynamicReportDto.builder()
            .outputReportType(reportDto.getOutputReportType()).fieldOrientationType(reportDto.getFieldOrientationType())
            .columns(reportDto.getColumns()).rows(Arrays.asList(row)).build();
        Vector<Object> elementsRowSubReport = new Vector<>();
        getRowsSubreport(rowSgiDynamicReportDto, columnIndex, elementsRowSubReport);
        elementsRow.add(elementsRowSubReport);
        break;
      case NUMBER:
      case STRING:
      default:
        elementsRow.add(row.getElements().get(columnIndex));
      }
    }
    return elementsRow;

  }

  private void getRowsSubreport(SgiDynamicReportDto reportDto, int columnIndex, Vector<Object> rows) {
    SgiRowReportDto row = reportDto.getRows().get(0);
    if (row.getElements().get(columnIndex) instanceof LinkedHashMap) {
      getRowsSubReportFromLinkedHashMap(reportDto, columnIndex, rows);
    } else if (row.getElements().get(columnIndex) instanceof SgiDynamicReportDto) {
      getRowsSubReportFromSgiDynamicReportDto(reportDto, columnIndex, rows);
    }
  }

  @SuppressWarnings("unchecked")
  private void getRowsSubReportFromLinkedHashMap(SgiDynamicReportDto reportDto, int columnIndex, Vector<Object> rows) {
    SgiRowReportDto row = reportDto.getRows().get(0);

    LinkedHashMap<String, List<Object>> elementsSubreport = (LinkedHashMap<String, List<Object>>) row.getElements()
        .get(columnIndex);
    List<Object> rowsSubreport = elementsSubreport.get("rows");
    for (Object objRowSubreport : rowsSubreport) {
      Vector<Object> elementsRow = new Vector<>();
      LinkedHashMap<String, List<Object>> hmRowSubreport = (LinkedHashMap<String, List<Object>>) objRowSubreport;
      for (Entry<String, List<Object>> entryRowSubreport : hmRowSubreport.entrySet()) {
        SgiRowReportDto rowSubreport = SgiRowReportDto.builder().elements(entryRowSubreport.getValue()).build();
        SgiDynamicReportDto rowSgiDynamicReportDto = SgiDynamicReportDto.builder()
            .outputReportType(reportDto.getOutputReportType()).fieldOrientationType(reportDto.getFieldOrientationType())
            .columns(reportDto.getColumns().get(columnIndex).getColumns()).rows(Arrays.asList(rowSubreport)).build();
        elementsRow.addAll(getRowsTableModelDynamic(rowSgiDynamicReportDto));
      }
      rows.add(elementsRow);
    }
  }

  private void getRowsSubReportFromSgiDynamicReportDto(SgiDynamicReportDto reportDto, int columnIndex,
      Vector<Object> rows) {
    SgiRowReportDto row = reportDto.getRows().get(0);

    SgiDynamicReportDto elementsSubreport = (SgiDynamicReportDto) row.getElements().get(columnIndex);
    List<SgiRowReportDto> rowsSubreport = elementsSubreport.getRows();
    for (SgiRowReportDto rowSubreport : rowsSubreport) {
      Vector<Object> elementsRow = new Vector<>();

      SgiDynamicReportDto rowSgiDynamicReportDto = SgiDynamicReportDto.builder()
          .outputReportType(reportDto.getOutputReportType()).fieldOrientationType(reportDto.getFieldOrientationType())
          .columns(reportDto.getColumns().get(columnIndex).getColumns()).rows(Arrays.asList(rowSubreport)).build();
      elementsRow.addAll(getRowsTableModelDynamic(rowSgiDynamicReportDto));
      rows.add(elementsRow);
    }
  }

  /**
   * Obtiene un subreport de tipo table-crud a partir de un tableModel
   * 
   * @param reportDto SgiReportDto
   * @return SubReport
   */
  protected SubReport getSubreportTableCrud(SgiDynamicReportDto reportDto) {
    Integer numColumns = reportDto.getDataModel().get(NAME_GENERAL_TABLE_MODEL).getColumnCount();
    setCustomWidthSubReport(reportDto, numColumns);

    final SubReport subreport = new SubReport();

    if (reportDto.getFieldOrientationType().equals(FieldOrientationType.HORIZONTAL)) {
      TableModel tableModel = reportDto.getDataModel().get(NAME_GENERAL_TABLE_MODEL);
      getHorizontalElements(reportDto, subreport, tableModel);
    } else {
      getVerticalElementsInnerSubReport(reportDto, subreport);
      getVerticalElementsSubReporTypeInnerSubReport(reportDto, subreport);
    }

    return subreport;
  }

  protected void getHorizontalElements(SgiDynamicReportDto reportDto, final SubReport subreport,
      TableModel tableModel) {
    float posX = 0f;
    float posY = 0f;
    float minimumWidth = reportDto.getColumnMinWidth();

    for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
      LabelElementFactory labelFactory = new LabelElementFactory();
      String fieldName = tableModel.getColumnName(columnIndex);
      SgiColumReportDto columnReportDto = reportDto.getColumns().stream().filter(c -> c.getName().equals(fieldName))
          .findFirst().orElseThrow(GetDataReportException::new);
      labelFactory.setText(columnReportDto.getTitle());
      labelFactory.setX(posX);
      labelFactory.setY(posY);
      labelFactory.setMinimumWidth(minimumWidth);
      labelFactory.setMinimumHeight(DYNAMIC_HEIGHT);
      labelFactory.setBorderTopStyle(BorderStyle.SOLID);
      labelFactory.setBorderWidth(0.5f);
      labelFactory.setVerticalAlignment(ElementAlignment.MIDDLE);
      labelFactory.setPaddingLeft(4f);
      labelFactory.setPaddingRight(4f);
      labelFactory.setPaddingTop(4f);
      labelFactory.setPaddingBottom(4f);
      labelFactory.setWrapText(Boolean.TRUE);
      labelFactory.setDynamicHeight(Boolean.FALSE);
      labelFactory.setBold(Boolean.TRUE);

      subreport.getReportHeader().addElement(labelFactory.createElement());

      FieldElementGeneratorDto fieldElementGeneratorDto = FieldElementGeneratorDto.builder().posX(posX)
          .minimumWidth(minimumWidth).fieldName(fieldName).build();

      if (CollectionUtils.isEmpty(reportDto.getColumns())
          || (!reportDto.getOutputReportType().equals(OutputReportType.XLS)
              && !reportDto.getOutputReportType().equals(OutputReportType.XLSX))) {
        TextFieldElementFactory textFieldElementFactory = new TextFieldElementFactory();
        initTextFieldHorizontalElement(textFieldElementFactory, fieldElementGeneratorDto);
        subreport.getItemBand().addElement(textFieldElementFactory.createElement());
      } else {
        SgiColumReportDto sgiColumReportDto = reportDto.getColumns().get(columnIndex);
        fieldElementGeneratorDto.setColumnReportDto(sgiColumReportDto);
        getElementByTypeForExcel(subreport, fieldElementGeneratorDto);
      }

      posX += minimumWidth;
    }

    String queryInner = QUERY_TYPE + SEPARATOR_KEY + NAME_QUERY_INNER + RandomStringUtils.randomAlphabetic(3);

    TableDataFactory dataFactorySubReportInner = new TableDataFactory();
    dataFactorySubReportInner.addTable(queryInner, tableModel);
    subreport.setDataFactory(dataFactorySubReportInner);

    subreport.setQuery(queryInner);
  }

  private void getElementByTypeForExcel(final SubReport subreport, FieldElementGeneratorDto fieldElementGeneratorDto) {
    switch (fieldElementGeneratorDto.getColumnReportDto().getType()) {
    case NUMBER:
      NumberFieldElementFactory numberFieldElementFactory = new NumberFieldElementFactory();
      initTextFieldHorizontalElement(numberFieldElementFactory, fieldElementGeneratorDto);
      subreport.getItemBand().addElement(numberFieldElementFactory.createElement());
      break;
    case DATE:
      DateFieldElementFactory dateFieldElementFactory = new DateFieldElementFactory();
      initTextFieldHorizontalElement(dateFieldElementFactory, fieldElementGeneratorDto);
      String format = StringUtils.isEmpty(fieldElementGeneratorDto.getColumnReportDto().getFormat())
          ? DATE_PATTERN_DEFAULT
          : fieldElementGeneratorDto.getColumnReportDto().getFormat();
      dateFieldElementFactory.setFormat(new SimpleDateFormat(format));
      subreport.getItemBand().addElement(dateFieldElementFactory.createElement());
      break;
    case SUBREPORT:
      log.debug("Not supported. The element is displayed as a empty string");
      LabelElementFactory labelElementFactory = new LabelElementFactory();
      initLabelHorizontalElement(labelElementFactory, fieldElementGeneratorDto);
      labelElementFactory.setText("");
      subreport.getItemBand().addElement(labelElementFactory.createElement());
      break;
    case STRING:
    default:
      TextFieldElementFactory textFieldElementFactory = new TextFieldElementFactory();
      initTextFieldHorizontalElement(textFieldElementFactory, fieldElementGeneratorDto);
      subreport.getItemBand().addElement(textFieldElementFactory.createElement());
    }
  }

  protected void initLabelHorizontalElement(LabelElementFactory label,
      FieldElementGeneratorDto fieldElementGeneratorDto) {
    label.setX(fieldElementGeneratorDto.getPosX());
    label.setY(0f);
    label.setMinimumWidth(fieldElementGeneratorDto.getMinimumWidth());
    label.setMinimumHeight(fieldElementGeneratorDto.getMinimumHeight());
    label.setBorderTopStyle(BorderStyle.SOLID);
    label.setBorderWidth(0.5f);
    label.setVerticalAlignment(ElementAlignment.MIDDLE);
    label.setPaddingLeft(4f);
    label.setPaddingRight(4f);
    label.setPaddingTop(4f);
    label.setPaddingBottom(4f);
    label.setWrapText(Boolean.TRUE);
  }

  protected void initTextFieldHorizontalElement(TextFieldElementFactory textFactoryProduct,
      FieldElementGeneratorDto fieldElementGeneratorDto) {
    textFactoryProduct.setFieldname(fieldElementGeneratorDto.getFieldName());
    textFactoryProduct.setX(fieldElementGeneratorDto.getPosX());
    textFactoryProduct.setY(0f);
    textFactoryProduct.setMinimumWidth(fieldElementGeneratorDto.getMinimumWidth());
    textFactoryProduct.setMinimumHeight(DYNAMIC_HEIGHT);
    textFactoryProduct.setBorderTopStyle(BorderStyle.SOLID);
    textFactoryProduct.setBorderWidth(0.5f);
    textFactoryProduct.setVerticalAlignment(ElementAlignment.MIDDLE);
    textFactoryProduct.setPaddingLeft(4f);
    textFactoryProduct.setPaddingRight(4f);
    textFactoryProduct.setPaddingTop(4f);
    textFactoryProduct.setPaddingBottom(4f);
    textFactoryProduct.setWrapText(Boolean.TRUE);
    textFactoryProduct.setDynamicHeight(Boolean.FALSE);
    textFactoryProduct.setNullString("");
  }

  protected DefaultTableModel getTableModelInner(TableModel tableModel, List<SgiColumReportDto> columns) {
    Vector<Object> columnsData = getColumsTableModelInnerSubReport();

    Vector<Vector<Object>> rowsData = new Vector<>();
    for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
      for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
        if (!columns.get(columnIndex).getType().equals(TypeColumnReportEnum.SUBREPORT)) {
          Vector<Object> elementsRow = new Vector<>();
          elementsRow.add(columns.get(columnIndex).getTitle() + ":");
          elementsRow.add(tableModel.getValueAt(rowIndex, columnIndex));
          rowsData.add(elementsRow);
        }
      }
    }

    DefaultTableModel tableModelInner = new DefaultTableModel();
    tableModelInner.setDataVector(rowsData, columnsData);
    return tableModelInner;
  }

  @SuppressWarnings("unchecked")
  protected TableModel getHorizontalTableModelInnerSubReportType(SgiColumReportDto columnSubReport,
      TableModel tableModel) {

    DefaultTableModel tableModelInner = null;
    Vector<Object> columnsData = new Vector<>();
    columnSubReport.getColumns().forEach(column -> columnsData.add(column.getName()));

    int indexColumnSubReport = getIndexColumnSubReport(columnSubReport, tableModel);

    if (indexColumnSubReport != 1) {
      Vector<Object> rowsSubReport = (Vector<Object>) tableModel.getValueAt(0, indexColumnSubReport);
      Vector<Vector<Object>> rowsData = new Vector<>();

      for (int rowIndex = 0; rowIndex < rowsSubReport.size(); rowIndex++) {
        Vector<Object> row = (Vector<Object>) rowsSubReport.get(rowIndex);
        rowsData.add(row);
      }
      tableModelInner = new DefaultTableModel();
      tableModelInner.setDataVector(rowsData, columnsData);
    }

    return tableModelInner;
  }

  @SuppressWarnings("unchecked")
  protected List<TableModel> getVerticalTableModelsInnerSubReportType(SgiColumReportDto columnSubReport,
      TableModel tableModel) {

    List<TableModel> tablesModelInner = new ArrayList<>();
    Vector<Object> columnsData = getColumsTableModelInnerSubReport();

    int indexColumnSubReport = getIndexColumnSubReport(columnSubReport, tableModel);

    if (indexColumnSubReport != 1) {

      try {

        Vector<Object> rowsSubReport = (Vector<Object>) tableModel.getValueAt(0, indexColumnSubReport);

        for (int rowIndex = 0; rowIndex < rowsSubReport.size(); rowIndex++) {
          Vector<Object> row = (Vector<Object>) rowsSubReport.get(rowIndex);
          Vector<Vector<Object>> rowsData = new Vector<>();
          for (int columnIndex = 0; columnIndex < columnSubReport.getColumns().size(); columnIndex++) {
            Vector<Object> elementsRow = new Vector<>();
            elementsRow.add(columnSubReport.getColumns().get(columnIndex).getTitle() + ":");
            elementsRow.add(row.get(columnIndex));
            rowsData.add(elementsRow);
          }
          DefaultTableModel tableModelInner = new DefaultTableModel();
          tableModelInner.setDataVector(rowsData, columnsData);
          tablesModelInner.add(tableModelInner);
        }
      } catch (Exception e) {
        log.debug(e.getMessage());
      }
    }

    return tablesModelInner;
  }

  private int getIndexColumnSubReport(SgiColumReportDto columnSubReport, TableModel tableModel) {
    int indexColumnSubReport = -1;
    for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
      if (tableModel.getColumnName(columnIndex).equals(columnSubReport.getName())) {
        indexColumnSubReport = columnIndex;
        break;
      }
    }
    return indexColumnSubReport;
  }

  private Vector<Object> getColumsTableModelInnerSubReport() {
    Vector<Object> columnsData = new Vector<>();
    columnsData.add(LABEL_FIELD_SUBREPORT);
    columnsData.add(VALUE_FIELD_SUBREPORT);
    return columnsData;
  }

  protected void getVerticalElementsInnerSubReport(SgiDynamicReportDto reportDto, final SubReport subreport) {
    TableModel tableModel = reportDto.getDataModel().get(NAME_GENERAL_TABLE_MODEL);

    DefaultTableModel tableModelInner = getTableModelInner(tableModel, reportDto.getColumns());

    final SubReport subreportInner = getInnerSubReportFromTableModel(reportDto, tableModelInner);

    subreport.getItemBand().addSubReport(subreportInner);

  }

  private SubReport getHeaderInnerSubReportFromTableModel(SgiDynamicReportDto reportDto,
      SgiColumReportDto columnSubReport) {
    float minimumWidth = reportDto.getCustomWidth();

    final SubReport subreportInner = new SubReport();
    TextFieldElementFactory labelElement = new TextFieldElementFactory();
    labelElement.setFieldname(TITLE_FIELD);
    labelElement.setX(0f);
    labelElement.setY(0f);
    labelElement.setMinimumWidth(minimumWidth);
    labelElement.setMinimumHeight(DYNAMIC_HEIGHT);
    labelElement.setBorderTopStyle(BorderStyle.SOLID);
    labelElement.setBorderWidth(0.5f);
    labelElement.setVerticalAlignment(ElementAlignment.MIDDLE);
    labelElement.setPaddingLeft(4f);
    labelElement.setPaddingRight(4f);
    labelElement.setPaddingTop(4f);
    labelElement.setPaddingBottom(4f);
    labelElement.setWrapText(Boolean.TRUE);
    labelElement.setBold(Boolean.TRUE);
    labelElement.setNullString("");
    labelElement.setBackgroundColor(Color.LIGHT_GRAY);

    subreportInner.getItemBand().addElement(labelElement.createElement());

    DefaultTableModel tableModelHeaderInner = getTableModelForLabel(columnSubReport.getTitle());

    String queryInner = QUERY_TYPE + SEPARATOR_KEY + NAME_QUERY_INNER;

    setDataFactoryToSubReport(subreportInner, tableModelHeaderInner, queryInner);

    return subreportInner;
  }

  private void setDataFactoryToSubReport(final SubReport subReport, TableModel tableModel, String query) {
    TableDataFactory dataFactorySubReportInner = new TableDataFactory();
    dataFactorySubReportInner.addTable(query, tableModel);
    subReport.setDataFactory(dataFactorySubReportInner);
    subReport.setQuery(query);
  }

  private SubReport getInnerSubReportFromTableModel(SgiDynamicReportDto reportDto, TableModel tableModelInner) {
    float minimumWidth = reportDto.getColumnMinWidth();

    final SubReport subreportInner = new SubReport();
    TextFieldElementFactory labelElement = new TextFieldElementFactory();
    labelElement.setFieldname(LABEL_FIELD_SUBREPORT);
    labelElement.setX(0f);
    labelElement.setY(0f);
    labelElement.setMinimumWidth(minimumWidth);
    labelElement.setMinimumHeight(DYNAMIC_HEIGHT);
    labelElement.setBorderTopStyle(BorderStyle.SOLID);
    labelElement.setBorderWidth(0.5f);
    labelElement.setPaddingLeft(4f);
    labelElement.setPaddingRight(4f);
    labelElement.setPaddingTop(4f);
    labelElement.setPaddingBottom(4f);
    labelElement.setWrapText(Boolean.TRUE);
    labelElement.setNullString("");
    labelElement.setBold(Boolean.TRUE);
    subreportInner.getItemBand().addElement(labelElement.createElement());

    TextFieldElementFactory valueElement = new TextFieldElementFactory();
    valueElement.setFieldname(VALUE_FIELD_SUBREPORT);
    valueElement.setX(minimumWidth);
    valueElement.setY(0f);
    valueElement.setMinimumWidth(reportDto.getCustomWidth() - minimumWidth);
    valueElement.setMinimumHeight(DYNAMIC_HEIGHT);
    valueElement.setBorderTopStyle(BorderStyle.SOLID);
    valueElement.setBorderWidth(0.5f);
    valueElement.setPaddingLeft(4f);
    valueElement.setPaddingRight(4f);
    valueElement.setPaddingTop(4f);
    valueElement.setPaddingBottom(4f);
    valueElement.setWrapText(Boolean.TRUE);
    valueElement.setNullString("");
    subreportInner.getItemBand().addElement(valueElement.createElement());

    String queryInner = QUERY_TYPE + SEPARATOR_KEY + NAME_QUERY_INNER;
    setDataFactoryToSubReport(subreportInner, tableModelInner, queryInner);

    return subreportInner;
  }

  protected void getVerticalElementsSubReporTypeInnerSubReport(SgiDynamicReportDto reportDto,
      final SubReport subreport) {
    TableModel tableModel = reportDto.getDataModel().get(NAME_GENERAL_TABLE_MODEL);

    List<SgiColumReportDto> columnsSubReport = reportDto.getColumns().stream()
        .filter(column -> column.getType().equals(TypeColumnReportEnum.SUBREPORT)).collect(Collectors.toList());

    if (!CollectionUtils.isEmpty(columnsSubReport)) {

      columnsSubReport.forEach(columnSubReport -> {
        final SubReport subreportHeaderInner = getHeaderInnerSubReportFromTableModel(reportDto, columnSubReport);
        subreport.getItemBand().addSubReport(subreportHeaderInner);

        if (null != columnSubReport.getFieldOrientationType()
            && columnSubReport.getFieldOrientationType().equals(FieldOrientationType.HORIZONTAL)
            && !reportDto.getOutputReportType().equals(OutputReportType.XLS)
            && !reportDto.getOutputReportType().equals(OutputReportType.XLSX)) {
          final SubReport subreportInner = new SubReport();
          TableModel horizontalTableModelInner = getHorizontalTableModelInnerSubReportType(columnSubReport, tableModel);

          SgiDynamicReportDto horizontalReportDto = SgiDynamicReportDto.builder()
              .customWidth(reportDto.getCustomWidth()).fieldOrientationType(columnSubReport.getFieldOrientationType())
              .columnMinWidth(reportDto.getColumnMinWidth()).outputReportType(reportDto.getOutputReportType())
              .columns(columnSubReport.getColumns()).build();

          Integer numColumns = columnSubReport.getColumns().size();
          setCustomWidthSubReport(horizontalReportDto, numColumns);

          getHorizontalElements(horizontalReportDto, subreportInner, horizontalTableModelInner);
          subreport.getItemBand().addSubReport(subreportInner);
        } else {

          List<TableModel> verticalTablesModelInner = getVerticalTableModelsInnerSubReportType(columnSubReport,
              tableModel);
          verticalTablesModelInner.forEach(tbInner -> {
            final SubReport subreportInner = getInnerSubReportFromTableModel(reportDto, tbInner);
            subreport.getItemBand().addSubReport(subreportInner);
          });
        }
      });
    }
  }

  protected void toJsonFile(SgiDynamicReportDto sgiDynamicReportDto) {
    try {
      String jsonSgiDynamicReportDto = sgiDynamicReportDto.toJson();
      File file = File.createTempFile("jsonSgiDynamicReportDto", ".json");
      FileUtils.writeStringToFile(file, jsonSgiDynamicReportDto);
    } catch (Exception e) {
      log.debug(e.getMessage());
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FieldElementGeneratorDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private float posX;
    private float minimumWidth;
    private float minimumHeight;
    private String fieldName;
    private SgiColumReportDto columnReportDto;
  }
}