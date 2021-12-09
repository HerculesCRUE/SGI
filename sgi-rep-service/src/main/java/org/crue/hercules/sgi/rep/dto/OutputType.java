package org.crue.hercules.sgi.rep.dto;

import com.google.common.net.MediaType;

/**
 * The supported output types for reports
 */
public enum OutputType {
  PDF(MediaType.PDF.toString()), CSV(MediaType.CSV_UTF_8.toString()), XLS(MediaType.MICROSOFT_EXCEL.toString()),
  XLSX(MediaType.MICROSOFT_EXCEL.toString()), HTML(MediaType.HTML_UTF_8.toString()),
  RTF(MediaType.RTF_UTF_8.toString());

  private final String type;

  private OutputType(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }
}