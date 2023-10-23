package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe acta
 */
public class ReportInformeActa extends SgiReportDto {
  public ReportInformeActa() {
    this.setPath("rep-eti-acta-docx");
    this.setName("informeActa");
  }
}