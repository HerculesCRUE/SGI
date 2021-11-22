package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.SgiReportDto;

/**
 * Instancia que contiene un report de informe acta
 */
public class ReportInformeActa extends SgiReportDto {
  public ReportInformeActa() {
    this.setPath("report/eti/informeActa.prpt");
    this.setName("informeActa");
  }
}