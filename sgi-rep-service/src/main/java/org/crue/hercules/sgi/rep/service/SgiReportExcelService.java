package org.crue.hercules.sgi.rep.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.ColumnType;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiColumReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiRowReportDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes
 */
@Service
@Slf4j
public class SgiReportExcelService {

  private static final String DATE_PATTERN_DEFAULT = "dd/MM/yyyy";
  private static final String ISO_LOCAL_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
  private static final String DEFAULT_TITLE = "Datos informe";
  private static final String NUMBER_PATTERN_DEFAULT = "0.00";
  private static final String DEFAULT_FORMAT_DOUBLE = "#.#,0";
  private static final String DEFAULT_FORMAT_INTEGER = "#";

  private final SgiConfigProperties sgiConfigProperties;
  private final SgiApiConfService sgiApiConfService;

  public SgiReportExcelService(SgiConfigProperties sgiConfigProperties, SgiApiConfService sgiApiConfService) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.sgiApiConfService = sgiApiConfService;
  }

  public byte[] export(SgiDynamicReportDto sgiReport) {
    log.debug("export(SgiDynamicReportDto sgiReport, byte[] headerImg) - start");
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] headerImg = getByteImageHeaderLogo();
    try {
      if (sgiReport.getOutputType().name().equalsIgnoreCase("csv")) {

        writeCsv(sgiReport, bos);

      } else {
        String title = sgiReport.getTitle();
        if (sgiReport.getTitle() == null) {
          title = DEFAULT_TITLE;
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(title);
        writeHeaderLogo(workbook, sheet, sgiReport, headerImg);
        writeHeaderLine(workbook, sheet, sgiReport);
        writeDataLines(workbook, sheet, sgiReport);

        workbook.write(bos);
        workbook.close();

        bos.close();
      }
    } catch (Exception e) {
      log.error("export(SgiDynamicReportDto sgiReport, byte[] headerImg) - end", e);
    }
    log.debug("export(SgiDynamicReportDto sgiReport, byte[] headerImg) - end");
    return bos.toByteArray();
  }

  private void writeHeaderLogo(XSSFWorkbook workbook, XSSFSheet sheet, SgiDynamicReportDto sgiReport,
      byte[] headerImgByte) {
    int headerImg = workbook.addPicture(headerImgByte, Workbook.PICTURE_TYPE_JPEG);
    ClientAnchor headerImgAnchor = new XSSFClientAnchor();
    headerImgAnchor.setCol1(0);
    headerImgAnchor.setCol2(1);
    headerImgAnchor.setRow1(0);
    headerImgAnchor.setRow2(1);
    Picture picture = sheet.createDrawingPatriarch().createPicture(headerImgAnchor, headerImg);
    picture.resize(4);
  }

  private void writeHeaderLine(XSSFWorkbook workbook, XSSFSheet sheet, SgiDynamicReportDto sgiReport) {
    XSSFRow row = sheet.createRow(5);

    int columnCount = 0;
    for (SgiColumReportDto col : sgiReport.getColumns()) {
      createCellHeader(workbook, sheet, row, columnCount++, col.getTitle());
    }

  }

  private void writeDataLines(XSSFWorkbook workbook, XSSFSheet sheet, SgiDynamicReportDto sgiReport) {
    int rowCount = 6;
    if (CollectionUtils.isNotEmpty(sgiReport.getRows())) {
      List<SgiRowReportDto> rowsReport = sgiReport.getRows();
      for (int k = 0; k < rowsReport.size(); k++) {
        XSSFRow row = sheet.createRow(rowCount++);
        SgiRowReportDto rowReport = rowsReport.get(k);
        for (int columnCount = 0; columnCount < rowReport.getElements().size(); columnCount++) {
          createCell(workbook, sheet, row, columnCount, rowReport.getElements().get(columnCount),
              sgiReport.getColumns().get(columnCount));
        }
      }
    }
  }

  private byte[] writeCsv(SgiDynamicReportDto sgiReport, ByteArrayOutputStream bos) throws IOException {
    List<String> headers = sgiReport.getColumns().stream().map(column -> column.getTitle())
        .collect(Collectors.toList());
    CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(headers.toArray(new String[headers.size()]));

    List<String> data = new ArrayList<>();

    CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(bos), csvFormat);
    if (CollectionUtils.isNotEmpty(sgiReport.getRows())) {
      List<SgiRowReportDto> rowsReport = sgiReport.getRows();
      for (int k = 0; k < rowsReport.size(); k++) {
        data = new ArrayList<>();
        SgiRowReportDto rowReport = rowsReport.get(k);
        for (int columnCount = 0; columnCount < rowReport.getElements().size(); columnCount++) {
          data.add(returnValue(rowReport.getElements().get(columnCount),
              sgiReport.getColumns().get(columnCount)));
        }
        csvPrinter.printRecord(data.toArray(new String[data.size()]));
      }
    }

    csvPrinter.flush();
    csvPrinter.close();
    bos.close();
    return bos.toByteArray();
  }

  private void createCellHeader(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, int columnCount, Object value) {
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    style.setBorderTop(BorderStyle.THIN);
    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
    XSSFFont font = workbook.createFont();
    font.setFontHeight((short) 200);
    font.setBold(true);
    style.setFont(font);
    sheet.autoSizeColumn(columnCount);
    Cell cell = row.createCell(columnCount);
    cell.setCellValue((String) value);
    cell.setCellStyle(style);
  }

  private void createCell(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, int columnCount, Object value,
      SgiColumReportDto columnReportDto) {
    CellStyle style = workbook.createCellStyle();
    style.setBorderBottom(BorderStyle.THIN);
    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
    XSSFFont font = workbook.createFont();
    font.setFontHeight((short) 200);
    style.setFont(font);
    sheet.autoSizeColumn(columnCount);
    Cell cell = row.createCell(columnCount);
    if (ObjectUtils.isNotEmpty(columnReportDto) && ObjectUtils.isNotEmpty(columnReportDto.getType())
        && ObjectUtils.isNotEmpty(value)) {
      switch (columnReportDto.getType()) {
        case DATE:
          if (StringUtils.hasText((String) value)) {
            CreationHelper createHelper = workbook.getCreationHelper();
            style.setDataFormat(
                createHelper.createDataFormat().getFormat(DATE_PATTERN_DEFAULT));
            try {
              Instant fechaInstant = Instant.parse((String) value);
              cell.setCellValue(formatInstantToDate(fechaInstant));
            } catch (DateTimeParseException e) {
              cell.setCellValue((String) value);
            }
          }
          break;
        case NUMBER:
          DataFormat format = workbook.createDataFormat();
          String stringFormat = DEFAULT_FORMAT_DOUBLE;
          if (!StringUtils.isEmpty(columnReportDto.getFormat())) {
            stringFormat = columnReportDto.getFormat();
          }
          if (value instanceof Integer) {
            String stringFormatInt = DEFAULT_FORMAT_INTEGER;
            if (!StringUtils.isEmpty(columnReportDto.getFormat())) {
              stringFormatInt = columnReportDto.getFormat();
            }
            style.setDataFormat(format.getFormat(stringFormatInt));
            cell.setCellValue((Integer) value);
          } else if (value instanceof Double) {
            style.setDataFormat(format.getFormat(stringFormat));
            cell.setCellValue((Double) value);
          } else if (value instanceof String) {
            style.setDataFormat(format.getFormat(stringFormat));
            String numberString = formatNumberString((String) value, columnReportDto.getFormat());
            cell.setCellValue(Double.parseDouble(numberString.replace(",", ".")));
          }
          break;
        default:
          try {
            cell.setCellValue((String) value);
          } catch (Exception e) {
            log.error(
                "createCell() - columnReportDto: " + columnReportDto.getTitle(), e);
            cell.setCellValue(value.toString());
          }
          break;
      }
    } else {
      cell.setCellValue((String) value);
    }
    cell.setCellStyle(style);
  }

  private String returnValue(Object value, SgiColumReportDto columnReportDto) {
    String data = "";

    if (ObjectUtils.isNotEmpty(columnReportDto) && ObjectUtils.isNotEmpty(columnReportDto.getType())
        && ObjectUtils.isNotEmpty(value)) {
      if (columnReportDto.getType().equals(ColumnType.DATE)) {
        if (StringUtils.hasText((String) value)) {
          try {
            Instant fechaInstant = Instant.parse((String) value);
            data = formatInstantToString(fechaInstant);
          } catch (DateTimeParseException e) {
            data = (String) value;
          }
        }
      } else {
        data = value.toString();
      }
    } else if (ObjectUtils.isNotEmpty(value)) {
      data = value.toString();
    }

    return escapeSpecialCharacters(data);
  }

  private String escapeSpecialCharacters(String data) {
    String escapedData = data.replaceAll("\\R", " ");
    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
      data = data.replace("\"", "\"\"");
      escapedData = "\"" + data + "\"";
    }
    return escapedData;
  }

  private String formatInstantToString(Instant instantDate, String pattern) {
    String result = "";

    if (null != instantDate) {
      pattern = StringUtils.hasText(pattern) ? pattern : DATE_PATTERN_DEFAULT;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
          .withZone(sgiConfigProperties.getTimeZone().toZoneId()).withLocale(LocaleContextHolder.getLocale());

      result = formatter.format(instantDate);
    }

    return result;
  }

  private Date formatInstantToDate(Instant instantDate) {
    Date result = null;
    try {
      String strFechaWithTimeZone = formatInstantToString(instantDate, ISO_LOCAL_DATE_PATTERN);
      SimpleDateFormat formatter = new SimpleDateFormat(ISO_LOCAL_DATE_PATTERN);
      result = formatter.parse(strFechaWithTimeZone);
    } catch (ParseException e) {
      log.error("formatInstantToDate(Instant instantDate)", e.getMessage());
    }

    return result;
  }

  private String formatNumberString(String numberString, String pattern) {
    String result = "";
    numberString = numberString.replace(",", ".");
    if (StringUtils.hasText(numberString) && NumberUtils.isParsable(numberString)) {
      pattern = StringUtils.hasText(pattern) ? pattern : NUMBER_PATTERN_DEFAULT;
      DecimalFormat decimalFormat = new DecimalFormat(pattern,
          DecimalFormatSymbols.getInstance(LocaleContextHolder.getLocale()));
      result = decimalFormat.format(Double.parseDouble(numberString));
    }

    return result;
  }

  private String formatInstantToString(Instant instantDate) {
    return formatInstantToString(instantDate, DATE_PATTERN_DEFAULT);
  }

  /**
   * Obtiene la imágen de la cabecera del informe
   * 
   * @return byte[]
   */
  private byte[] getByteImageHeaderLogo() {
    try {
      return sgiApiConfService.getResource("rep-common-header-logo");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

}