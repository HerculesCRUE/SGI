package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report M10, M20 o M30
 */
public class ReportMXX extends SgiReportDto {
  public ReportMXX() {
    this.setPath("rep-eti-mxx-docx");
    this.setName("mxx");
  }
}