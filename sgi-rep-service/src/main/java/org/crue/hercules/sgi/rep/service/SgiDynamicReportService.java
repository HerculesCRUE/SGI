package org.crue.hercules.sgi.rep.service;

import java.awt.Color;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.validation.Valid;

import org.apache.commons.lang3.RandomStringUtils;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.OutputType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.ColumnType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiColumReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiGroupReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiRowReportDto;
import org.crue.hercules.sgi.rep.dto.SgiReportDto.FieldOrientation;
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

  public byte[] generateDynamicReport(@Valid SgiDynamicReportDto sgiReport) {
    log.debug("generateDynamicReport(sgiReport) - start");

    try {

      setReportConfiguration(sgiReport);

      MasterReport report = getReportDefinition(sgiReport.getPath());

      setTableModelGeneral(sgiReport, report);

      if (null != sgiReport.getFilters()) {
        setTableModelFilters(sgiReport, report);
      }

      setTableModelDetails(sgiReport, report);

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return sgiReport.getContent();
  }

  protected void setReportConfiguration(SgiDynamicReportDto sgiReport) {

    if (null == sgiReport.getFieldOrientation()) {
      sgiReport.setFieldOrientation(FieldOrientation.HORIZONTAL);
    }

    if (null == sgiReport.getCustomWidth()) {
      sgiReport.setCustomWidth(WIDTH_PORTRAIT);
    }

    if (sgiReport.getCustomWidth().compareTo(WIDTH_PORTRAIT) > 0) {
      if (sgiReport.getOutputType().equals(OutputType.PDF) || sgiReport.getOutputType().equals(OutputType.RTF)) {
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
    if (sgiReport.getFieldOrientation().equals(FieldOrientation.VERTICAL)) {
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
      SubReport subReportTableCrud = getDynamicSubReportTableCrud(sgiReport, queryDetails, tableModelName, rowIndex);

      report.getItemBand().addSubReport(subReportTableCrud);
    }
  }

  protected void getDynamicSubReportTableCrudHorizontal(SgiDynamicReportDto sgiReport, final MasterReport report) {
    String queryDetails = QUERY_TYPE + SEPARATOR_KEY + NAME_DETAILS_TABLE_MODEL;
    DefaultTableModel tableModelDetails = getTableModelDynamic(sgiReport);

    Map<String, TableModel> hmTableModel = new HashMap<>();
    hmTableModel.put(NAME_GENERAL_TABLE_MODEL, tableModelDetails);
    sgiReport.setDataModel(hmTableModel);
    SubReport subReportTableCrud = getDynamicSubReportTableCrud(sgiReport, queryDetails, NAME_GENERAL_TABLE_MODEL, 0);

    report.getItemBand().addSubReport(subReportTableCrud);
  }

  private SubReport getDynamicSubReportTableCrud(SgiDynamicReportDto sgiReport, String queryDetails,
      String tableModelName, Integer rowIndex) {
    SubReport subReportTableCrud = getSubreportTableCrud(sgiReport, rowIndex);
    setDataFactoryToSubReport(subReportTableCrud, sgiReport.getDataModel().get(tableModelName), queryDetails);

    if (null != sgiReport.getGroupBy()
        && sgiReport.getFieldOrientation().equals(FieldOrientation.VERTICAL)
        && StringUtils.hasText(sgiReport.getGroupBy().getName())) {
      addGroup((RelationalGroup) subReportTableCrud.getGroup(0), sgiReport);
    }
    return subReportTableCrud;
  }

  protected void addGroup(RelationalGroup group, SgiDynamicReportDto sgiReport) {
    SgiGroupReportDto groupReportDto = sgiReport.getGroupBy();

    Integer numColumns = 1;
    if (!CollectionUtils.isEmpty(sgiReport.getGroupBy().getAdditionalGroupNames())) {
      numColumns += sgiReport.getGroupBy().getAdditionalGroupNames().size();
    }

    group.setFields(Arrays.asList(sgiReport.getGroupBy().getName()));
    group.setName(NAME_GROUP + sgiReport.getGroupBy().getName());
    if (null != groupReportDto.getVisible() && groupReportDto.getVisible().equals(Boolean.TRUE)) {
      float minimumWidth = sgiReport.getCustomWidth() / numColumns;
      String fieldGroupName = sgiReport.getGroupBy().getName();
      TextFieldElementFactory textFieldGroupName = getTextFieldGroupColumn(minimumWidth, fieldGroupName);

      textFieldGroupName.setX(0f);

      group.getHeader().addElement(textFieldGroupName.createElement());
      addAdditionalGroupNames(group, sgiReport, minimumWidth);
    }
  }

  private void addAdditionalGroupNames(RelationalGroup group, SgiDynamicReportDto sgiReport, float minimumWidth) {
    if (!CollectionUtils.isEmpty(sgiReport.getGroupBy().getAdditionalGroupNames())) {
      int numAdditionalsGroupsName = sgiReport.getGroupBy().getAdditionalGroupNames().size();
      IntStream.range(0, numAdditionalsGroupsName).forEach(indexGroupName -> {
        String additionalGroupName = sgiReport.getGroupBy().getAdditionalGroupNames().get(indexGroupName);

        TextFieldElementFactory textFieldAdditionalGroupName = getTextFieldGroupColumn(minimumWidth,
            additionalGroupName);

        float posXAdditionalGroup = (indexGroupName + 1) * minimumWidth;
        textFieldAdditionalGroupName.setX(posXAdditionalGroup);
        if (indexGroupName == numAdditionalsGroupsName - 1) {
          textFieldAdditionalGroupName.setHorizontalAlignment(ElementAlignment.RIGHT);
        } else {
          textFieldAdditionalGroupName.setHorizontalAlignment(ElementAlignment.LEFT);
        }
        group.getHeader().addElement(textFieldAdditionalGroupName.createElement());
      });
    }
  }

  private TextFieldElementFactory getTextFieldGroupColumn(float minimumWidth, String fieldGroupName) {
    TextFieldElementFactory textFactoryProduct = new TextFieldElementFactory();
    textFactoryProduct.setFieldname(fieldGroupName);

    textFactoryProduct.setMinimumWidth(minimumWidth);
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
    textFactoryProduct.setY(0f);
    return textFactoryProduct;
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

      SgiDynamicReportDto rowSgiDynamicReportDto = SgiDynamicReportDto.builder().outputType(sgiReport.getOutputType())
          .fieldOrientation(sgiReport.getFieldOrientation()).columns(sgiReport.getColumns()).rows(Arrays.asList(row))
          .build();
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
          elementsRow.add(parseElementDate(reportDto, row, columnIndex, sgiColumReportDto));
          break;
        case SUBREPORT:
          SgiDynamicReportDto rowSgiDynamicReportDto = SgiDynamicReportDto.builder()
              .outputType(reportDto.getOutputType())
              .fieldOrientation(reportDto.getFieldOrientation()).columns(reportDto.getColumns())
              .rows(Arrays.asList(row))
              .build();
          Vector<Object> elementsRowSubReport = new Vector<>();
          getRowsSubreport(rowSgiDynamicReportDto, columnIndex, elementsRowSubReport);
          elementsRow.add(elementsRowSubReport);
          break;
        case NUMBER:
          elementsRow.add(parseElementNumber(reportDto, row, columnIndex, sgiColumReportDto));
          break;
        case STRING:
        default:
          elementsRow.add(row.getElements().get(columnIndex));
      }
    }
    return elementsRow;

  }

  private Object parseElementNumber(SgiDynamicReportDto reportDto, SgiRowReportDto row, int columnIndex,
      SgiColumReportDto sgiColumReportDto) {
    Object result = "";
    if (reportDto.getOutputType().equals(OutputType.XLS) || reportDto.getOutputType().equals(OutputType.XLSX)) {
      result = row.getElements().get(columnIndex);
    } else {
      Object element = row.getElements().get(columnIndex);
      if (null != element) {
        result = formatNumberString(element.toString(), sgiColumReportDto.getFormat());
      }
    }
    return result;
  }

  private Object parseElementDate(SgiDynamicReportDto reportDto, SgiRowReportDto row, int columnIndex,
      SgiColumReportDto sgiColumReportDto) {
    Object result = "";

    if (StringUtils.hasText((String) row.getElements().get(columnIndex))) {
      Instant fechaInstant = Instant.parse((String) row.getElements().get(columnIndex));
      if ((reportDto.getOutputType().equals(OutputType.XLS) || reportDto.getOutputType().equals(OutputType.XLSX))
          && (reportDto.getFieldOrientation().equals(FieldOrientation.HORIZONTAL))) {
        result = formatInstantToDate(fechaInstant);
      } else {
        result = formatInstantToString(fechaInstant, sgiColumReportDto.getFormat());
      }
    }
    return result;
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
        SgiDynamicReportDto rowSgiDynamicReportDto = SgiDynamicReportDto.builder().outputType(reportDto.getOutputType())
            .fieldOrientation(reportDto.getFieldOrientation())
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

      SgiDynamicReportDto rowSgiDynamicReportDto = SgiDynamicReportDto.builder().outputType(reportDto.getOutputType())
          .fieldOrientation(reportDto.getFieldOrientation())
          .columns(reportDto.getColumns().get(columnIndex).getColumns()).rows(Arrays.asList(rowSubreport)).build();
      elementsRow.addAll(getRowsTableModelDynamic(rowSgiDynamicReportDto));
      rows.add(elementsRow);
    }
  }

  /**
   * Obtiene un subreport de tipo table-crud a partir de un tableModel
   * 
   * @param reportDto SgiReportDto
   * @param rowIndex  index of de row evaluated
   * @return SubReport
   */
  protected SubReport getSubreportTableCrud(SgiDynamicReportDto reportDto, Integer rowIndex) {
    Integer numColumns = reportDto.getDataModel().get(NAME_GENERAL_TABLE_MODEL).getColumnCount();
    setCustomWidthSubReport(reportDto, numColumns);

    final SubReport subreport = new SubReport();

    if (reportDto.getFieldOrientation().equals(FieldOrientation.HORIZONTAL)) {
      TableModel tableModel = reportDto.getDataModel().get(NAME_GENERAL_TABLE_MODEL);
      getHorizontalElements(reportDto, subreport, tableModel);
    } else {
      getVerticalElementsInnerSubReport(reportDto, subreport);
      getVerticalElementsSubReporTypeInnerSubReport(reportDto, subreport, rowIndex);
    }

    return subreport;
  }

  protected void getHorizontalElements(SgiDynamicReportDto reportDto, final SubReport subreport,
      TableModel tableModel) {
    float posX = 0f;
    float posY = 0f;
    float minimumWidth = reportDto.getColumnMinWidth();

    for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
      String fieldName = tableModel.getColumnName(columnIndex);
      SgiColumReportDto columnReportDto = reportDto.getColumns().stream().filter(c -> c.getName().equals(fieldName))
          .findFirst().orElseThrow(GetDataReportException::new);

      if (null == columnReportDto.getVisible() || Boolean.TRUE.equals(columnReportDto.getVisible())) {
        Float columnWidth = getColumnWidth(minimumWidth, reportDto.getCustomWidth(), columnReportDto);

        LabelElementFactory labelFactory = getLabelElementColumn(columnReportDto, columnWidth);
        labelFactory.setX(posX);
        labelFactory.setY(posY);

        subreport.getReportHeader().addElement(labelFactory.createElement());

        FieldElementGeneratorDto fieldElementGeneratorDto = FieldElementGeneratorDto.builder()
            .posX(posX)
            .minimumWidth(columnWidth)
            .fieldName(fieldName)
            .columnReportDto(columnReportDto)
            .build();

        if (CollectionUtils.isEmpty(reportDto.getColumns()) || (!reportDto.getOutputType().equals(OutputType.XLS)
            && !reportDto.getOutputType().equals(OutputType.XLSX))) {
          TextFieldElementFactory textFieldElementFactory = new TextFieldElementFactory();
          initTextFieldHorizontalElement(textFieldElementFactory, fieldElementGeneratorDto);
          subreport.getItemBand().addElement(textFieldElementFactory.createElement());
        } else {
          SgiColumReportDto sgiColumReportDto = reportDto.getColumns().get(columnIndex);
          fieldElementGeneratorDto.setColumnReportDto(sgiColumReportDto);
          getElementByTypeForExcel(subreport, fieldElementGeneratorDto);
        }

        posX += columnWidth;
      }
    }

    String queryInner = QUERY_TYPE + SEPARATOR_KEY + NAME_QUERY_INNER + RandomStringUtils.randomAlphabetic(3);

    TableDataFactory dataFactorySubReportInner = new TableDataFactory();
    dataFactorySubReportInner.addTable(queryInner, tableModel);
    subreport.setDataFactory(dataFactorySubReportInner);

    subreport.setQuery(queryInner);
  }

  private LabelElementFactory getLabelElementColumn(SgiColumReportDto columnReportDto, Float columnWidth) {
    LabelElementFactory labelFactory = initLabelHeader();
    String title = StringUtils.hasText(columnReportDto.getTitle()) ? columnReportDto.getTitle()
        : columnReportDto.getName();
    labelFactory.setText(title);
    labelFactory.setMinimumWidth(columnWidth);
    labelFactory.setHorizontalAlignment(getElementAlignment(columnReportDto));

    return labelFactory;
  }

  private LabelElementFactory initLabelHeader() {
    LabelElementFactory labelFactory = new LabelElementFactory();

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

    return labelFactory;
  }

  private Float getColumnWidth(Float minimumWidth, Float reportWidth, SgiColumReportDto columnReportDto) {
    Float columnWidth = minimumWidth;
    if (null != columnReportDto.getHorizontalOptions()
        && null != columnReportDto.getHorizontalOptions().getCustomWidth()) {
      columnWidth = reportWidth * columnReportDto.getHorizontalOptions().getCustomWidth() / 100;
    }
    return columnWidth;
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
    if (null != fieldElementGeneratorDto.getColumnReportDto()) {
      textFactoryProduct.setHorizontalAlignment(getElementAlignment(fieldElementGeneratorDto.getColumnReportDto()));
    }
    textFactoryProduct.setPaddingLeft(4f);
    textFactoryProduct.setPaddingRight(4f);
    textFactoryProduct.setPaddingTop(4f);
    textFactoryProduct.setPaddingBottom(4f);
    textFactoryProduct.setWrapText(Boolean.TRUE);
    textFactoryProduct.setDynamicHeight(Boolean.FALSE);
    textFactoryProduct.setNullString("");
  }

  private ElementAlignment getElementAlignment(SgiColumReportDto columReportDto) {
    if (null != columReportDto.getHorizontalOptions()
        && null != columReportDto.getHorizontalOptions().getElementAlignmentType()) {
      switch (columReportDto.getHorizontalOptions().getElementAlignmentType()) {
        case RIGHT:
          return ElementAlignment.RIGHT;
        case CENTER:
          return ElementAlignment.CENTER;
        case TOP:
          return ElementAlignment.TOP;
        case MIDDLE:
          return ElementAlignment.MIDDLE;
        case BOTTOM:
          return ElementAlignment.BOTTOM;
        case JUSTIFY:
          return ElementAlignment.JUSTIFY;
        case LEFT:
        default:
          return ElementAlignment.LEFT;
      }
    }
    return ElementAlignment.LEFT;
  }

  protected DefaultTableModel getTableModelInner(TableModel tableModel, List<SgiColumReportDto> columns) {
    Vector<Object> columnsData = getColumsTableModelInnerSubReport();

    Vector<Vector<Object>> rowsData = new Vector<>();
    for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
      for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
        SgiColumReportDto columReportDto = columns.get(columnIndex);
        if (!columReportDto.getType().equals(ColumnType.SUBREPORT)
            && (null == columReportDto.getVisible() || Boolean.TRUE.equals(columReportDto.getVisible()))) {
          Vector<Object> elementsRow = new Vector<>();
          String title = StringUtils.hasText(columns.get(columnIndex).getTitle()) ? columns.get(columnIndex).getTitle()
              : columns.get(columnIndex).getName();
          elementsRow.add(title + ":");
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
  protected TableModel getHorizontalTableModelInnerSubReportType(SgiColumReportDto subReportColumn,
      TableModel tableModel) {

    DefaultTableModel tableModelInner = null;
    Vector<Object> columnsData = new Vector<>();
    subReportColumn.getColumns().forEach(column -> columnsData.add(column.getName()));

    int columnIndexSubReport = getcolumnIndexSubReport(subReportColumn, tableModel);

    if (columnIndexSubReport != 1) {
      Vector<Object> rowsSubReport = (Vector<Object>) tableModel.getValueAt(0, columnIndexSubReport);
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
  protected List<TableModel> getVerticalTableModelsInnerSubReportType(SgiColumReportDto subReportColumn,
      TableModel tableModel) {

    List<TableModel> tablesModelInner = new ArrayList<>();
    Vector<Object> columnsData = getColumsTableModelInnerSubReport();

    int columnIndexSubReport = getcolumnIndexSubReport(subReportColumn, tableModel);

    if (columnIndexSubReport != 1) {
      try {
        Vector<Object> rowsSubReport = (Vector<Object>) tableModel.getValueAt(0, columnIndexSubReport);
        for (int rowIndex = 0; rowIndex < rowsSubReport.size(); rowIndex++) {
          Vector<Vector<Object>> rowsData = getDataVerticalTableModelsInnerSubReportType(subReportColumn, rowsSubReport,
              rowIndex);
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

  @SuppressWarnings("unchecked")
  private Vector<Vector<Object>> getDataVerticalTableModelsInnerSubReportType(SgiColumReportDto subReportColumn,
      Vector<Object> rowsSubReport, int rowIndex) {
    Vector<Vector<Object>> rowsData = new Vector<>();

    Vector<Object> row = (Vector<Object>) rowsSubReport.get(rowIndex);
    for (int columnIndex = 0; columnIndex < subReportColumn.getColumns().size(); columnIndex++) {
      Vector<Object> elementsRow = new Vector<>();
      String suffixElementIndex = rowsSubReport.size() > 1 ? " " + (rowIndex + 1) + ":" : "";
      String title = StringUtils.hasText(subReportColumn.getColumns().get(columnIndex).getTitle())
          ? subReportColumn.getColumns().get(columnIndex).getTitle()
          : subReportColumn.getColumns().get(columnIndex).getName();
      elementsRow.add(title + suffixElementIndex);
      elementsRow.add(row.get(columnIndex));
      rowsData.add(elementsRow);
    }
    return rowsData;
  }

  private int getcolumnIndexSubReport(SgiColumReportDto subReportColumn, TableModel tableModel) {
    int columnIndexSubReport = -1;
    for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
      if (tableModel.getColumnName(columnIndex).equals(subReportColumn.getName())) {
        columnIndexSubReport = columnIndex;
        break;
      }
    }
    return columnIndexSubReport;
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
      SgiColumReportDto subReportColumn) {
    int numColumns = 1;
    if (!CollectionUtils.isEmpty(subReportColumn.getAdditionalTitle())) {
      numColumns += subReportColumn.getAdditionalTitle().size();
    }
    float minimumWidth = reportDto.getCustomWidth() / numColumns;

    final SubReport subreportInner = new SubReport();
    LabelElementFactory labelElement = initLabelHeader();
    String title = StringUtils.hasText(subReportColumn.getTitle()) ? subReportColumn.getTitle()
        : subReportColumn.getName();
    labelElement.setText(title);
    labelElement.setX(0f);
    labelElement.setY(0f);
    labelElement.setMinimumWidth(minimumWidth);
    labelElement.setBackgroundColor(Color.LIGHT_GRAY);
    subreportInner.getItemBand().addElement(labelElement.createElement());

    if (!CollectionUtils.isEmpty(subReportColumn.getAdditionalTitle())) {
      int numAdditionalsGroupsName = subReportColumn.getAdditionalTitle().size();
      IntStream.range(0, numAdditionalsGroupsName).forEach(indexAdditionalTitle -> {
        String additionalTitle = subReportColumn.getAdditionalTitle().get(indexAdditionalTitle);
        LabelElementFactory labelAdditionalElement = initLabelHeader();
        labelAdditionalElement.setText(additionalTitle);
        labelAdditionalElement.setY(0f);
        labelAdditionalElement.setMinimumWidth(minimumWidth);
        labelAdditionalElement.setBackgroundColor(Color.LIGHT_GRAY);

        float posXAdditionalTitle = (indexAdditionalTitle + 1) * minimumWidth;
        labelAdditionalElement.setX(posXAdditionalTitle);
        if (indexAdditionalTitle == numAdditionalsGroupsName - 1) {
          labelAdditionalElement.setHorizontalAlignment(ElementAlignment.RIGHT);
        } else {
          labelAdditionalElement.setHorizontalAlignment(ElementAlignment.LEFT);
        }
        subreportInner.getItemBand().addElement(labelAdditionalElement.createElement());
      });

    }

    DefaultTableModel tableModelHeaderInner = getTableModelForLabel(subReportColumn.getTitle());

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
      final SubReport subreport, Integer rowIndex) {
    TableModel tableModel = reportDto.getDataModel().get(NAME_GENERAL_TABLE_MODEL);

    if (!CollectionUtils.isEmpty(reportDto.getColumns())) {

      IntStream.range(0, reportDto.getColumns().size()).forEach(columnIndex -> {

        SgiColumReportDto subReportColumn = reportDto.getColumns().get(columnIndex);

        if (subReportColumn.getType().equals(ColumnType.SUBREPORT) &&
            isSubReportVisible(reportDto, rowIndex, columnIndex)) {

          final SubReport subreportHeaderInner = getHeaderInnerSubReportFromTableModel(reportDto, subReportColumn);
          subreport.getItemBand().addSubReport(subreportHeaderInner);

          if (isHorizontalAndNotExcel(reportDto, subReportColumn)) {
            final SubReport subreportInner = new SubReport();
            TableModel horizontalTableModelInner = getHorizontalTableModelInnerSubReportType(subReportColumn,
                tableModel);

            SgiDynamicReportDto horizontalReportDto = SgiDynamicReportDto.builder()
                .customWidth(reportDto.getCustomWidth()).fieldOrientation(subReportColumn.getFieldOrientation())
                .outputType(reportDto.getOutputType())
                .columns(subReportColumn.getColumns()).build();

            Integer numColumns = subReportColumn.getColumns().stream()
                .filter(column -> column.getVisible() == null || Boolean.TRUE.equals(column.getVisible()))
                .mapToInt(e -> 1).sum();

            setCustomWidthSubReport(horizontalReportDto, numColumns);

            getHorizontalElements(horizontalReportDto, subreportInner, horizontalTableModelInner);
            subreport.getItemBand().addSubReport(subreportInner);
          } else {

            List<TableModel> verticalTablesModelInner = getVerticalTableModelsInnerSubReportType(subReportColumn,
                tableModel);
            verticalTablesModelInner.forEach(tbInner -> {
              final SubReport subreportInner = getInnerSubReportFromTableModel(reportDto, tbInner);
              subreport.getItemBand().addSubReport(subreportInner);
            });
          }
        }
      });
    }
  }

  private boolean isHorizontalAndNotExcel(SgiDynamicReportDto reportDto, SgiColumReportDto subReportColumn) {
    return null != subReportColumn.getFieldOrientation()
        && subReportColumn.getFieldOrientation().equals(FieldOrientation.HORIZONTAL)
        && !reportDto.getOutputType().equals(OutputType.XLS)
        && !reportDto.getOutputType().equals(OutputType.XLSX);
  }

  @SuppressWarnings("unchecked")
  private boolean isSubReportVisible(SgiDynamicReportDto reportDto, Integer rowIndex, Integer columnIndex) {
    SgiColumReportDto subReportColumn = reportDto.getColumns().get(columnIndex);

    boolean isVisible = (null == subReportColumn.getVisibleIfSubReportEmpty()
        || Boolean.TRUE.equals(subReportColumn.getVisibleIfSubReportEmpty()))
        && (null == reportDto.getHideBlocksIfNoData()
            || Boolean.FALSE.equals(reportDto.getHideBlocksIfNoData()));

    if (!isVisible) {
      LinkedHashMap<String, List<Object>> elementsSubreport = (LinkedHashMap<String, List<Object>>) reportDto
          .getRows()
          .get(rowIndex).getElements().get(columnIndex);
      isVisible = !CollectionUtils.isEmpty(elementsSubreport.get("rows"));
    }

    return isVisible;
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